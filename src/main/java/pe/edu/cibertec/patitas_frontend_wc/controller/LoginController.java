package pe.edu.cibertec.patitas_frontend_wc.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.cibertec.patitas_frontend_wc.dto.LoginRequestDTO;
import pe.edu.cibertec.patitas_frontend_wc.dto.LoginResponseDTO;
import pe.edu.cibertec.patitas_frontend_wc.viewmodel.LoginModel;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/login")
public class LoginController {

    /*@Autowired
    RestTemplate restTemplateAutenticacion;*/

    @Autowired
    WebClient webClientAutenticacion;

    @GetMapping("/inicio")
    public String inicio(Model model){
        LoginModel loginModel = new LoginModel("00", "", "");
        model.addAttribute("loginmodel", loginModel);
        return "inicio";
    };

    @PostMapping("/autenticar")
    public String autenticar(@RequestParam("tipoDocumento") String tipoDocumento,
                                 @RequestParam("numeroDocumento") String numeroDocumento,
                                 @RequestParam("password") String password,
                                 Model model) {

        // Validar campos de entrada
        if (    tipoDocumento == null || tipoDocumento.trim().length() == 0 ||
                numeroDocumento == null || numeroDocumento.trim().length() == 0 ||
                password == null || password.trim().length() == 0
        ){
            LoginModel loginModel = new LoginModel("01", "Error: Deben completar correctamente sus credenciales", "");
            model.addAttribute("loginmodel", loginModel);
            return "inicio";
        }

        try {
            // Invocar API de validacion de usuario
            LoginRequestDTO loginRequestDTO = new LoginRequestDTO(tipoDocumento, numeroDocumento, password);

            Mono<LoginResponseDTO> monoLoginResponseDTO =  webClientAutenticacion.post()
                    .uri("/login")
                    .body(Mono.just(loginRequestDTO), LoginRequestDTO.class)
                    .retrieve()
                    .bodyToMono(LoginResponseDTO.class);

            // recuperar resultado del mono (sincrono o bloqueante)
            LoginResponseDTO loginResponseDTO = monoLoginResponseDTO.block();

            // Validar Respuesta
            if (loginResponseDTO.codigo().equals("00")) {

                LoginModel loginModel = new LoginModel("00", "", loginResponseDTO.nombreUsuario());
                model.addAttribute("loginmodel", loginModel);
                return "principal";
            } else {

                LoginModel loginModel = new LoginModel("02", "Error: Autenticacion fallida", "");
                model.addAttribute("loginmodel", loginModel);
                return "inicio";
            }
        } catch (Exception e) {

            LoginModel loginModel = new LoginModel("99", "Error: Ocurrio un problema en la autenticacion", "");
            model.addAttribute("loginmodel", loginModel);
            System.out.println(e.getMessage());
            return "inicio";
        }




    }
}
