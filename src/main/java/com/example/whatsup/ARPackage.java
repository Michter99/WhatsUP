package com.example.whatsup;

import java.io.Serializable;

public class ARPackage implements Serializable {
    public String idCertificado;
    public String nombreCertificado;
    public int llavePublica;
    public int puertoOrigenCliente;
    public int puertoOrigenAR;
    public boolean certificadoEncontrado;
}