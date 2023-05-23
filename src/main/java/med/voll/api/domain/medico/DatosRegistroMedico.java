package med.voll.api.domain.medico;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import med.voll.api.domain.direccion.DatosDireccion;

public record DatosRegistroMedico (

        @NotBlank(message = "{nombre.obligatorio}")
        String nombre,
        @NotBlank//contiene not null
        @Email
        String email,
        @NotBlank
        @Pattern(regexp = "\\d{4,6}")//expresion regular de 4 a 6 digitos
        String documento,
        @NotBlank
        String telefono,
        @NotNull
        Especialidad especialidad,
        @NotNull//como es un objeto puede llegar nulo mas no en blanco
        @Valid//valide los campos del objeto
        DatosDireccion direccion){
}
