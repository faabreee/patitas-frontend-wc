package pe.edu.cibertec.patitas_frontend_wc.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LoginResponseDTO(String codigo, String mensaje, String nombreUsuario, String correoUsuario) {
}
