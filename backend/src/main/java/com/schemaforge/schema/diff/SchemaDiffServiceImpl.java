package com.schemaforge.schema.diff;

import com.schemaforge.common.exception.BadRequestException;
import com.schemaforge.common.exception.ForbiddenException;
import com.schemaforge.project.repository.ProjectRepository;
import com.schemaforge.schema.entity.SchemaVersion;
import com.schemaforge.schema.exception.SchemaNotFoundException;
import com.schemaforge.schema.exception.SchemaVersionNotFoundException;
import com.schemaforge.schema.repository.SchemaRepository;
import com.schemaforge.schema.repository.SchemaVersionRepository;
import com.schemaforge.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SchemaDiffServiceImpl implements SchemaDiffService {

    private final SchemaRepository schemaRepository;
    private final SchemaVersionRepository schemaVersionRepository;
    private final ProjectRepository projectRepository;

    @Override
    @Transactional(readOnly = true)
    public SchemaDiffResponse diffVersions(
            User currentUser, UUID schemaId, int fromVersion, int toVersion
    ) {
        if (fromVersion == toVersion) {
            throw new BadRequestException("fromVersion and toVersion must be different");
        }

        // Ownership check
        schemaRepository.findActiveByIdAndOwnerId(schemaId, currentUser.getId())
                .orElseThrow(() -> new SchemaNotFoundException(schemaId));

        SchemaVersion from = schemaVersionRepository
                .findBySchemaIdAndVersionNumber(schemaId, fromVersion)
                .orElseThrow(() -> new SchemaVersionNotFoundException(schemaId, fromVersion));

        SchemaVersion to = schemaVersionRepository
                .findBySchemaIdAndVersionNumber(schemaId, toVersion)
                .orElseThrow(() -> new SchemaVersionNotFoundException(schemaId, toVersion));

        return computeDiff(schemaId, fromVersion, toVersion,
                from.getSnapshot(), to.getSnapshot());
    }

    @SuppressWarnings("unchecked")
    private SchemaDiffResponse computeDiff(
            UUID schemaId, int fromVersion, int toVersion,
            Map<String, Object> fromSnap, Map<String, Object> toSnap
    ) {
        List<Map<String, Object>> fromTables = extractList(fromSnap, "tables");
        List<Map<String, Object>> toTables   = extractList(toSnap, "tables");

        Map<String, Map<String, Object>> fromMap = indexByName(fromTables);
        Map<String, Map<String, Object>> toMap   = indexByName(toTables);

        List<String> tablesAdded   = toMap.keySet().stream()
                .filter(n -> !fromMap.containsKey(n)).sorted().toList();
        List<String> tablesRemoved = fromMap.keySet().stream()
                .filter(n -> !toMap.containsKey(n)).sorted().toList();

        List<TableDiff> tablesModified = new ArrayList<>();
        for (String name : fromMap.keySet()) {
            if (!toMap.containsKey(name)) continue;
            TableDiff diff = diffTable(name, fromMap.get(name), toMap.get(name));
            if (!diff.columnsAdded().isEmpty()
                    || !diff.columnsRemoved().isEmpty()
                    || !diff.columnsModified().isEmpty()) {
                tablesModified.add(diff);
            }
        }

        List<Map<String, Object>> fromRels = extractList(fromSnap, "relationships");
        List<Map<String, Object>> toRels   = extractList(toSnap, "relationships");

        Set<String> fromRelKeys = relKeys(fromRels);
        Set<String> toRelKeys   = relKeys(toRels);

        List<String> relsAdded   = toRelKeys.stream()
                .filter(r -> !fromRelKeys.contains(r)).sorted().toList();
        List<String> relsRemoved = fromRelKeys.stream()
                .filter(r -> !toRelKeys.contains(r)).sorted().toList();

        int totalChanges = tablesAdded.size() + tablesRemoved.size()
                + tablesModified.size() + relsAdded.size() + relsRemoved.size();

        return new SchemaDiffResponse(schemaId, fromVersion, toVersion,
                tablesAdded, tablesRemoved, tablesModified,
                relsAdded, relsRemoved, totalChanges);
    }

    private TableDiff diffTable(
            String name,
            Map<String, Object> fromTable,
            Map<String, Object> toTable
    ) {
        Map<String, Map<String, Object>> fromFields = indexByName(extractList(fromTable, "fields"));
        Map<String, Map<String, Object>> toFields   = indexByName(extractList(toTable, "fields"));

        List<ColumnDiff> added    = new ArrayList<>();
        List<ColumnDiff> removed  = new ArrayList<>();
        List<ColumnDiff> modified = new ArrayList<>();

        for (String fieldName : toFields.keySet()) {
            if (!fromFields.containsKey(fieldName)) {
                var f = toFields.get(fieldName);
                added.add(new ColumnDiff(fieldName, null, str(f, "type"),
                        false, null, constraintStr(f)));
            }
        }

        for (String fieldName : fromFields.keySet()) {
            var fromField = fromFields.get(fieldName);
            if (!toFields.containsKey(fieldName)) {
                removed.add(new ColumnDiff(fieldName, str(fromField, "type"), null,
                        false, constraintStr(fromField), null));
            } else {
                var toField = toFields.get(fieldName);
                String oldType  = str(fromField, "type");
                String newType  = str(toField, "type");
                String oldCons  = constraintStr(fromField);
                String newCons  = constraintStr(toField);
                boolean typeChg = !Objects.equals(oldType, newType);
                boolean consChg = !Objects.equals(oldCons, newCons);
                if (typeChg || consChg) {
                    modified.add(new ColumnDiff(fieldName,
                            oldType, newType, consChg, oldCons, newCons));
                }
            }
        }

        return new TableDiff(name, added, removed, modified);
    }

    // ── Helpers ─────────────────────────────────────────────────────────────────

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> extractList(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v instanceof List<?> list ? (List<Map<String, Object>>) list : List.of();
    }

    private Map<String, Map<String, Object>> indexByName(List<Map<String, Object>> items) {
        Map<String, Map<String, Object>> index = new HashMap<>();
        for (var item : items) {
            String n = str(item, "name");
            if (n != null) index.put(n, item);
        }
        return index;
    }

    private Set<String> relKeys(List<Map<String, Object>> rels) {
        Set<String> keys = new LinkedHashSet<>();
        for (var rel : rels) {
            keys.add(str(rel, "from") + "->" + str(rel, "to") + ":" + str(rel, "type"));
        }
        return keys;
    }

    @SuppressWarnings("unchecked")
    private String constraintStr(Map<String, Object> field) {
        Object c = field.get("constraints");
        if (c instanceof List<?> list) {
            return list.stream().map(Object::toString).sorted().toList().toString();
        }
        return "[]";
    }

    private String str(Map<String, Object> map, String key) {
        Object v = map.get(key);
        return v != null ? v.toString() : null;
    }
}