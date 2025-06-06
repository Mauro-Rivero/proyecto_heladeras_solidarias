package com.tp_anual.proyecto_heladeras_solidarias.colaborador;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.tp_anual.proyecto_heladeras_solidarias.exception.colaborador.ContribucionNoPermitidaException;
import com.tp_anual.proyecto_heladeras_solidarias.exception.contribucion.*;
import com.tp_anual.proyecto_heladeras_solidarias.exception.heladera.*;
import com.tp_anual.proyecto_heladeras_solidarias.exception.tarjeta.DatosInvalidosCrearTarjetaColaboradorException;
import com.tp_anual.proyecto_heladeras_solidarias.model.colaborador.ColaboradorHumano;
import com.tp_anual.proyecto_heladeras_solidarias.model.persona.PersonaFisica;
import com.tp_anual.proyecto_heladeras_solidarias.model.tarjeta.TarjetaColaborador;
import com.tp_anual.proyecto_heladeras_solidarias.service.colaborador.ColaboradorService;
import com.tp_anual.proyecto_heladeras_solidarias.service.heladera.HeladeraService;
import com.tp_anual.proyecto_heladeras_solidarias.service.heladera.ViandaService;
import com.tp_anual.proyecto_heladeras_solidarias.service.tarjeta.TarjetaColaboradorService;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.Contribucion;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.DistribucionViandas;
import com.tp_anual.proyecto_heladeras_solidarias.service.contribucion.DistribucionViandasCreator;
import com.tp_anual.proyecto_heladeras_solidarias.model.contribucion.DonacionVianda;
import com.tp_anual.proyecto_heladeras_solidarias.service.contribucion.DonacionViandaCreator;
import com.tp_anual.proyecto_heladeras_solidarias.model.documento.Documento;
import com.tp_anual.proyecto_heladeras_solidarias.model.documento.Documento.Sexo;
import com.tp_anual.proyecto_heladeras_solidarias.model.documento.Documento.TipoDocumento;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.Heladera;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.vianda.Vianda;
import com.tp_anual.proyecto_heladeras_solidarias.model.heladera.acciones_en_heladera.SolicitudAperturaColaborador;
import com.tp_anual.proyecto_heladeras_solidarias.model.ubicacion.Ubicacion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class Colaborador2Test {

    @Autowired
    ColaboradorService colaboradorService;

    @Autowired
    HeladeraService heladeraService;

    @Autowired
    ViandaService viandaService;

    @Autowired
    TarjetaColaboradorService tarjetaColaboradorService;

    @Autowired
    DonacionViandaCreator donacionViandaCreator;

    @Autowired
    DistribucionViandasCreator distribucionViandasCreator;


    @Test
    @DisplayName("Testeo la correcta creación de Contribuciones y que se agreguen a las contribuciones del Colaborador")
    public void ColaborarTest() throws ContribucionNoPermitidaException, DatosInvalidosCrearCargaOfertaException, DatosInvalidosCrearDistribucionViandasException, DatosInvalidosCrearDonacionDineroException, DatosInvalidosCrearDonacionViandaException, DatosInvalidosCrearHCHException, DatosInvalidosCrearRPESVException, DomicilioFaltanteDiVsException, DomicilioFaltanteDoVException, DomicilioFaltanteRPESVException, DatosInvalidosCrearTarjetaColaboradorException, HeladeraVaciaSolicitudRetiroException, HeladeraLlenaSolicitudIngresoException, HeladeraLlenaAgregarViandaException, PermisoAperturaExpiradoException, PermisoAperturaAusenteException, HeladeraVaciaRetirarViandaException {
        ColaboradorHumano colaboradorHumano = new ColaboradorHumano(null, new PersonaFisica("NombrePrueba", "ApellidoPrueba", new Documento(TipoDocumento.DNI, "40123456", Sexo.MASCULINO), LocalDate.parse("2003-01-01")), new Ubicacion(-34.6083, -58.3709, "Balcarce 78", "1064", "Ciudad Autónoma de Buenos Aires", "Argentina"), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), 0d); // Uso ColaboradorHumano porque Colaborador es abstract y el metodo es igual para ambos (Humano y Juridico)
        Long colaboradorHumanoId = colaboradorService.guardarColaborador(colaboradorHumano).getId();

        LocalDateTime fechaAperturaH1 = LocalDateTime.parse("2024-01-01T00:00:00");
        LocalDateTime fechaAperturaH2 = LocalDateTime.parse("2024-02-01T00:00:00");
        LocalDate fechaCaducidadV = LocalDate.parse("2025-01-01");

        Heladera heladera1 = new Heladera("HeladeraPrueba", new Ubicacion(-34.601978, -58.383865, "Tucumán 1171", "1049", "Ciudad Autónoma de Buenos Aires", "Argentina"), 20, -20f, 5f, new ArrayList<>(), 3f, fechaAperturaH1 , true);
        Heladera heladera2 = new Heladera("HeladeraPrueba2", new Ubicacion(-34.6092, -58.3842, "Avenida de Mayo 1370", "1086", "Ciudad Autónoma de Buenos Aires", "Argentina"), 20, -20f, 5f, new ArrayList<>(), 5f, fechaAperturaH2, true);

        Long heladera1Id = heladeraService.guardarHeladera(heladera1).getId();
        Long heladera2Id = heladeraService.guardarHeladera(heladera2).getId();

        Vianda vianda = new Vianda("ComidaPrueba" , colaboradorHumano, fechaCaducidadV, null, 0, 0, false);
        Long viandaId = viandaService.guardarVianda(vianda).getId();

        DonacionVianda donacionVianda = (DonacionVianda) colaboradorService.colaborar(colaboradorHumanoId, donacionViandaCreator, LocalDateTime.now(), vianda, heladera1);

        TarjetaColaborador tarjetaColaborador = tarjetaColaboradorService.crearTarjeta(colaboradorHumanoId);
        String codigoTarjeta = colaboradorService.agregarTarjeta(colaboradorHumanoId, tarjetaColaborador).getCodigo();

        tarjetaColaboradorService.solicitarApertura(codigoTarjeta, heladera1, SolicitudAperturaColaborador.MotivoSolicitud.INGRESAR_DONACION, 1);
        tarjetaColaboradorService.intentarApertura(codigoTarjeta, heladera1);
        heladeraService.agregarVianda(heladera1Id, vianda);
        viandaService.agregarAHeladeraPrimeraVez(viandaId, heladera1);

        colaboradorService.confirmarContribucion(colaboradorHumanoId, donacionVianda, LocalDateTime.now());

        DistribucionViandas distribucionViandas = (DistribucionViandas) colaboradorService.colaborar(colaboradorHumanoId, distribucionViandasCreator, LocalDateTime.now(), heladera1, heladera2, 1, DistribucionViandas.MotivoDistribucion.FALTA_DE_VIANDAS_EN_DESTINO);

        tarjetaColaboradorService.solicitarApertura(codigoTarjeta, heladera1, SolicitudAperturaColaborador.MotivoSolicitud.RETIRAR_LOTE_DE_DISTRIBUCION, 1);
        tarjetaColaboradorService.intentarApertura(codigoTarjeta, heladera1);
        heladeraService.retirarVianda(heladera1Id);
        viandaService.quitarDeHeladera(viandaId);

        tarjetaColaboradorService.solicitarApertura(codigoTarjeta ,heladera2, SolicitudAperturaColaborador.MotivoSolicitud.INGRESAR_LOTE_DE_DISTRIBUCION, 1);
        tarjetaColaboradorService.intentarApertura(codigoTarjeta, heladera2);
        heladeraService.agregarVianda(heladera2Id, vianda);
        viandaService.agregarAHeladera(viandaId, heladera2);

        colaboradorService.confirmarContribucion(colaboradorHumanoId, distribucionViandas, LocalDateTime.now());

        List<Contribucion> contribuciones = new ArrayList<>();
        contribuciones.add(donacionVianda);
        contribuciones.add(distribucionViandas);

        Assertions.assertThat(colaboradorHumano.getContribuciones())
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyInAnyOrderElementsOf(contribuciones);
    }
}
