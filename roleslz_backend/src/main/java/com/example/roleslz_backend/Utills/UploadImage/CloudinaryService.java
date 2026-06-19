package com.example.roleslz_backend.Utills.UploadImage;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class CloudinaryService implements StorageFile {

    private final Cloudinary cloudinary;

    // Injeção das credenciais via construtor para criar a instância do Cloudinary.
    // Esta é uma abordagem mais limpa do que ter um método de configuração separado na mesma classe.
    public CloudinaryService(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret) {
        
        Map<String, String> config = ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        );
        this.cloudinary = new Cloudinary(config);
    }

    @Override
    public List<String> uploadMultipleFiles(List<MultipartFile> files) {
        return files.stream()
                .map(this::uploadFile)
                .collect(Collectors.toList());
    }

    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("O arquivo não pode ser nulo ou vazio.");
        }
        try {
            // O 'resource_type: "auto"' permite que o Cloudinary identifique o tipo de arquivo.
            // Isso torna o método genérico para imagens, vídeos, etc.
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap("resource_type", "auto"));
            
            // O Cloudinary retorna a URL segura no campo "secure_url".
            return (String) uploadResult.get("secure_url");
            
        } catch (IOException e) {
            // Lançamos uma exceção de runtime para não forçar o chamador a tratar IOException.
            // Isso é comum na camada de serviço do Spring.
            throw new RuntimeException("Falha ao fazer upload do arquivo para o Cloudinary", e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            throw new IllegalArgumentException("A URL do arquivo não pode ser nula ou vazia.");
        }
        try {
            // Para deletar, precisamos do 'public_id' do arquivo, não da URL completa.
            String publicId = extractPublicIdFromUrl(fileUrl);
            
            // Usamos 'resource_type: "image"' como padrão, mas poderia ser "video" ou "raw".
            // Se você armazena outros tipos, pode ser necessário inferir o tipo a partir da URL.
            cloudinary.uploader().destroy(publicId, ObjectUtils.asMap("resource_type", "image"));

        } catch (IOException e) {
            throw new RuntimeException("Falha ao deletar o arquivo do Cloudinary", e);
        }
    }

    /**
     * Extrai o public_id de uma URL do Cloudinary.
     * Ex: "http://res.cloudinary.com/cloud/image/upload/v123/folder/public_id.jpg" -> "folder/public_id"
     * @param url A URL completa do recurso.
     * @return O public_id extraído.
     */
    private String extractPublicIdFromUrl(String url) {
        // Regex para encontrar o caminho do arquivo após a parte ".../upload/" e remover a extensão.
        Pattern pattern = Pattern.compile("/upload/(?:v\\d+/)?(.*?)(?:\\.\\w+)?$");
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }
        throw new IllegalArgumentException("URL do Cloudinary inválida. Não foi possível extrair o public_id.");
    }
}
