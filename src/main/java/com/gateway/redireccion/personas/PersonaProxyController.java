package com.gateway.redireccion.personas;

import org.springframework.http.HttpHeaders;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/proxy")
@RequiredArgsConstructor
public class PersonaProxyController {

    private final RestTemplate restTemplate;

    @RequestMapping(value = "/**", method = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
    public ResponseEntity<?> proxyProductos(HttpServletRequest request,
                                            @RequestBody(required = false) String body,
                                            @RequestHeader HttpHeaders headers) {

        // Construir ruta final
        String originalPath = request.getRequestURI().replace("/api/proxy/personas", "");
        String targetUrl = "http://localhost:8082/api/v1/personas" + originalPath;

        HttpMethod method = HttpMethod.valueOf(request.getMethod());

        // Clonar headers y limpiar los conflictivos
        HttpHeaders cleanHeaders = new HttpHeaders();
        headers.forEach((key, value) -> {
            if (!key.equalsIgnoreCase(HttpHeaders.CONTENT_LENGTH)) {
                cleanHeaders.put(key, value);
            }
        });
        cleanHeaders.setContentType(MediaType.APPLICATION_JSON); // importante

        // Crear la entidad a enviar
        HttpEntity<String> entity = new HttpEntity<>(body, cleanHeaders);

        // Redirigir al microservicio
        ResponseEntity<String> response = restTemplate.exchange(targetUrl, method, entity, String.class);

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
    
}
