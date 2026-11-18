package com.example.roleslz_backend.Utills;

import java.net.URI;

public final class UrlUtils {

    private UrlUtils() {}

    /**
     * Normaliza um link informado pelo organizador (ex.: link de ingresso).
     * Vazio/nulo -> null. Sem esquema -> assume https://. Inválido -> IllegalArgumentException.
     */
    public static String normalizar(String url) {
        if (url == null) return null;
        String limpo = url.trim();
        if (limpo.isEmpty()) return null;

        if (!limpo.matches("(?i)^[a-z][a-z0-9+.-]*://.*")) {
            limpo = "https://" + limpo;
        }

        try {
            URI uri = new URI(limpo);
            if (uri.getHost() == null || !uri.getHost().contains(".")) {
                throw new IllegalArgumentException("Link inválido. Use um endereço como https://exemplo.com/ingressos");
            }
            return limpo;
        } catch (java.net.URISyntaxException e) {
            throw new IllegalArgumentException("Link inválido. Use um endereço como https://exemplo.com/ingressos");
        }
    }
}
