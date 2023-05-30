package med.voll.api.controller;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import med.voll.api.domain.direccion.DatosDireccion;
import med.voll.api.domain.medico.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/medicos")
public class MedicoController {

    @Autowired
    private MedicoRepository medicoRepository;

    @PostMapping
    //retorna 201 creatred
    //retorna URL para contrar el obj http://localhost:8081/medic/{id}
    //este metodo va a retorna un objeto dto en el response entity
    public ResponseEntity<DatosRespuestaMedico> registrarMedico(@RequestBody @Valid DatosRegistroMedico datosRegistroMedico,
                                                                UriComponentsBuilder uriComponentsBuilder){
        System.out.println("El request es exitoso");
        System.out.println(datosRegistroMedico);
       Medico medico = medicoRepository.save(new Medico(datosRegistroMedico));
       //necesito los datos desde el dto
       DatosRespuestaMedico datosRespuestaMedico = new DatosRespuestaMedico(medico.getId(), medico.getNombre(),
               medico.getEmail(), medico.getTelefono(), medico.getEspecialidad().toString(),
               new DatosDireccion(medico.getDireccion().getCalle(), medico.getDireccion().getDistrito(),
                       medico.getDireccion().getCiudad(), medico.getDireccion().getNumero(),
                       medico.getDireccion().getComplemento()));

       //url del objeto
        URI url = uriComponentsBuilder.path("/medico/{id}").buildAndExpand(medico.getId()).toUri();
        return ResponseEntity.created(url).body(datosRespuestaMedico);

    }

//    @GetMapping //con todos los datos
//    public List<Medico> listadoMedicos(){
//        return medicoRepository.findAll();
//    }


//    @GetMapping//mostrar la lista de datos segun las reglas de negocio
//    public List<DatosListadoMedico> listadoMedicos(){
//        return medicoRepository.findAll().stream().map(DatosListadoMedico::new).toList();//listado con cada medico nuevo
//    }

    //por regla de negocio debe listar solo los que esten activos para ello se crea una query
//    @GetMapping//mostrar la lista de datos segun las reglas de negocio
//    public Page<DatosListadoMedico> listadoMedicos(@PageableDefault(size = 1) Pageable paginacion){//sobre escribe el valor default de spring y el cliente a su vez los puede cambiar
//        return medicoRepository.findByActivoTrue(paginacion).map(DatosListadoMedico::new);//listado con cada medico nuevo
//    }


    @GetMapping
    public ResponseEntity<Page<DatosListadoMedico>> listadoMedicos(@PageableDefault(size = 1) Pageable paginacion){//sobre escribe el valor default de spring y el cliente a su vez los puede cambiar
        return ResponseEntity.ok(medicoRepository.findByActivoTrue(paginacion).map(DatosListadoMedico::new));//listado con cada medico nuevo
    }




//    @GetMapping//mostrar la lista de datos segun las reglas de negocio
//    public Page<DatosListadoMedico> listadoMedicos(@PageableDefault(size = 2) Pageable paginacion){//sobre escribe el valor default de spring y el cliente a su vez los puede cambiar
//        return medicoRepository.findAll(paginacion).map(DatosListadoMedico::new);//listado con cada medico nuevo
//    }

    //se debe crear un nuevo dto ya que por regla de negocio no se actualiza todo
    @PutMapping
    @Transactional//cuando termina el metodo hace el commit en la bd y libera la tx
    public ResponseEntity actualizarMedico(@RequestBody @Valid DatosActualizarMedico datosActualizarMedico){

        Medico medico = medicoRepository.getReferenceById(datosActualizarMedico.id());
        medico.actualizarDatos(datosActualizarMedico);

        return ResponseEntity.ok(new DatosRespuestaMedico(medico.getId(), medico.getNombre(),
                medico.getEmail(), medico.getTelefono(), medico.getEspecialidad().toString(),
                new DatosDireccion(medico.getDireccion().getCalle(), medico.getDireccion().getDistrito(),
                        medico.getDireccion().getCiudad(), medico.getDireccion().getNumero(),
                        medico.getDireccion().getComplemento())));
    }

    //delete logico
    @DeleteMapping("/{id}")//variable dinamica en el path
    @Transactional
    public ResponseEntity eliminarMedico(@PathVariable Long id){//path variable para que tome lo que recibe del path
        Medico medico = medicoRepository.getReferenceById(id);
        medico.desactivarMedico();
        return ResponseEntity.noContent().build();//devuelve un tipo 204
    }

    //delete de la bd
//    public void eliminarMedico(@PathVariable Long id){//path variable para que tome lo que recibe del path
//        Medico medico = medicoRepository.getReferenceById(id);
//        medicoRepository.delete(medico);
//
//    }
    //get no necesita transactional
    @GetMapping("/{id}")//variable dinamica en el path
    public ResponseEntity<DatosRespuestaMedico> retornaDatosMedico(@PathVariable Long id){//path variable para que tome lo que recibe del path
        Medico medico = medicoRepository.getReferenceById(id);
        var datosMedico = new DatosRespuestaMedico(medico.getId(), medico.getNombre(),
                medico.getEmail(), medico.getTelefono(), medico.getEspecialidad().toString(),
                new DatosDireccion(medico.getDireccion().getCalle(), medico.getDireccion().getDistrito(),
                        medico.getDireccion().getCiudad(), medico.getDireccion().getNumero(),
                        medico.getDireccion().getComplemento()));
        return ResponseEntity.ok(datosMedico);
    }

    //                  METODO CON EL ROL ADMIN @Secured para el rol
    /*
    @GetMapping("/{id}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity detallar(@PathVariable Long id) {
        var medico = medicoRepository.getReferenceById(id);
        var datosMedico = new DatosRespuestaMedico(medico.getId(), medico.getNombre(),
                medico.getEmail(), medico.getTelefono(), medico.getEspecialidad().toString(),
                new DatosDireccion(medico.getDireccion().getCalle(), medico.getDireccion().getDistrito(),
                        medico.getDireccion().getCiudad(), medico.getDireccion().getNumero(),
                        medico.getDireccion().getComplemento()));
        return ResponseEntity.ok(datosMedico);
    }
    */
}
