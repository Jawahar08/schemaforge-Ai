package com.schemaforge.ai.client;

import com.schemaforge.ai.entity.AiProvider;
import com.schemaforge.ai.exception.UnsupportedAiProviderException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class AiProviderClientFactory {

    private final List<AiProviderClient> clients;

    private Map<AiProvider, AiProviderClient> clientsByProvider;

    private Map<AiProvider, AiProviderClient> getClientsByProvider() {
        if (clientsByProvider == null) {
            clientsByProvider = clients.stream()
                    .collect(Collectors.toMap(AiProviderClient::getProviderType, Function.identity()));
        }
        return clientsByProvider;
    }

    public AiProviderClient getClient(AiProvider provider) {
        AiProviderClient client = getClientsByProvider().get(provider);
        if (client == null) {
            throw new UnsupportedAiProviderException(provider != null ? provider.name() : "null");
        }
        return client;
    }
}