package com.tp_anual.proyecto_heladeras_solidarias.colaborador;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Locale;
import java.util.NoSuchElementException;

import com.tp_anual.proyecto_heladeras_solidarias.exception.tarjeta.DatosInvalidosCrearTarjetaPESVException;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorHumano;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorJuridico;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.persona.PersonaFisica;
import com.tp_anual.proyecto_heladeras_solidarias.service.colaborador.ColaboradorService;
import com.tp_anual.proyecto_heladeras_solidarias.service.persona_en_situacion_vulnerable.PersonaEnSituacionVulnerableService;
import com.tp_anual.proyecto_heladeras_solidarias.service.tarjeta.TarjetaPersonaEnSituacionVulnerableService;
import com.tp_anual.proyecto_heladeras_solidarias.utils.SpringContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;

import com.tp_anual.proyecto_heladeras_solidarias.model.contacto.EMail;
import com.tp_anual.proyecto_heladeras_solidarias.model.contacto.WhatsApp;
import com.tp_anual.proyecto_heladeras_solidarias.service.contribucion.HacerseCargoDeHeladeraCreator;
import com.tp_anual.proyecto_heladeras_solidarias.service.contribucion.RegistroDePersonaEnSituacionVulnerableCreator;
import com.tp_anual.proyecto_heladeras_solidarias.model.documento.Documento;
import com.tp_anual.proyecto_heladeras_solidarias.model.documento.Documento.Sexo;
import com.tp_anual.proyecto_heladeras_solidarias.model.documento.Documento.TipoDocumento;
import com.tp_anual.proyecto_heladeras_solidarias.model.persona.PersonaJuridica;
import com.tp_anual.proyecto_heladeras_solidarias.model.persona_en_situacion_vulnerable.PersonaEnSituacionVulnerable;
import com.tp_anual.proyecto_heladeras_solidarias.model.tarjeta.TarjetaPersonaEnSituacionVulnerable;
import com.tp_anual.proyecto_heladeras_solidarias.model.ubicacion.Ubicacion;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.MessageSource;

@SpringBootTest
public class Colaborador1Test {

    @Autowired
    ColaboradorService colaboradorService;

    @Autowired
    TarjetaPersonaEnSituacionVulnerableService tarjetaPersonaEnSituacionVulnerableService;

    @Autowired
    PersonaEnSituacionVulnerableService personaEnSituacionVulnerableService;

    @Autowired
    HacerseCargoDeHeladeraCreator hacerseCargoDeHeladeraCreator;

    @Autowired
    RegistroDePersonaEnSituacionVulnerableCreator registroDePersonaEnSituacionVulnerableCreator;

    ColaboradorHumano colaboradorHumano;
    ColaboradorJuridico colaboradorJuridico;
    EMail eMail;
    Long colaboradorHumanoId;
    Long colaboradorJuridicoId;

    @BeforeEach
    void setup() {
        colaboradorHumano = new ColaboradorHumano(null, new PersonaFisica("NombrePrueba", "ApellidoPrueba", new Documento(TipoDocumento.DNI, "40123456", Sexo.MASCULINO), LocalDate.parse("2003-01-01")), new Ubicacion(-34.6083, -58.3709, "Balcarce 78", "1064", "Ciudad Autónoma de Buenos Aires", "Argentina"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0d);
        colaboradorJuridico = new ColaboradorJuridico(null, new PersonaJuridica("RazonSocialPrueba", PersonaJuridica.TipoPersonaJuridica.EMPRESA, "RubroPrueba"), new Ubicacion(-34.6098, -58.3925, "Avenida Entre Ríos", "1033", "Ciudad Autónoma de Buenos Aires", "Argentina"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0d);

        colaboradorHumanoId = colaboradorService.guardarColaborador(colaboradorHumano).getId();
        colaboradorJuridicoId = colaboradorService.guardarColaborador(colaboradorJuridico).getId();

        eMail = new EMail("correoprueba@gmail.com");
        colaboradorService.agregarMedioDeContacto(colaboradorHumanoId, eMail);
    }

    @Test
    @DisplayName("Testeo la obtención de un Contacto del Colaborador")
    public void GetContactoTest() {
        Assertions.assertEquals(colaboradorHumano.getMedioDeContacto(EMail.class), eMail);  // Uso ColaboradorHumano porque Colaborador es abstract y el metodo es igual para ambos (Humano y Juridico)
    }

    @Test
    @DisplayName("Testeo la NoSuchElementException al pasar un MedioDeContacto que no posee el Colaborador")
    public void NoSuchElementGetContactoTest() {
        NoSuchElementException exception = Assertions.assertThrows(NoSuchElementException.class, () -> colaboradorHumano.getMedioDeContacto(WhatsApp.class));

        MessageSource messageSource = SpringContext.getBean(MessageSource.class);
        String exceptionMessage = messageSource.getMessage("colaborador.Colaborador.getContacto_exception", null, Locale.getDefault());

        Assertions.assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Testeo la IllegalArgumentException para Contribuciones que el ColaboradorHumano no es capaz de hacer")
    public void IllegalArgumentColaborarCHTest() {
        Heladera heladera = new Heladera("HeladeraPrueba", new Ubicacion(-34.601978, -58.383865, "Tucumán 1171", "1049", "Ciudad Autónoma de Buenos Aires", "Argentina"), 20, -20f, 5f, new ArrayList<>(), 3f, LocalDateTime.now() , true);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> colaboradorService.colaborar(colaboradorHumanoId, hacerseCargoDeHeladeraCreator, LocalDateTime.now(), heladera));

        MessageSource messageSource = SpringContext.getBean(MessageSource.class);
        String exceptionMessage = messageSource.getMessage("colaborador.Colaborador.colaborar_exception", null, Locale.getDefault());

        Assertions.assertEquals(exceptionMessage, exception.getMessage());
    }

    @Test
    @DisplayName("Testeo la IllegalArgumentException para Contribuciones que el ColaboradorJuridico no es capaz de hacer")
    public void IllegalArgumentColaborarCJTest() throws DatosInvalidosCrearTarjetaPESVException {
        PersonaEnSituacionVulnerable personaEnSituacionVulnerable = new PersonaEnSituacionVulnerable(new PersonaFisica("NombrePruebaPESV", "ApellidoPruebaPESV", new Documento(TipoDocumento.DNI, "40123450", Sexo.MASCULINO), LocalDate.parse("2003-01-01")), new Ubicacion(-34.63927052902741, -58.50938609197106, "Avenida Rivadavia 10357", "1408", "Ciudad Autónoma de Buenos Aires", "Argentina"), LocalDateTime.now(), 2, true);
        Long personaEnSituacionVulnerableID = personaEnSituacionVulnerableService.guardarPersonaEnSituacionVulnerable(personaEnSituacionVulnerable).getId();
        TarjetaPersonaEnSituacionVulnerable tarjeta = tarjetaPersonaEnSituacionVulnerableService.crearTarjeta(personaEnSituacionVulnerableID);

        IllegalArgumentException exception = Assertions.assertThrows(IllegalArgumentException.class, () -> colaboradorService.colaborar(colaboradorJuridicoId, registroDePersonaEnSituacionVulnerableCreator, LocalDateTime.now(), tarjeta));

        MessageSource messageSource = SpringContext.getBean(MessageSource.class);
        String exceptionMessage = messageSource.getMessage("colaborador.Colaborador.colaborar_exception", null, Locale.getDefault());

        Assertions.assertEquals(exceptionMessage, exception.getMessage());
    }
}