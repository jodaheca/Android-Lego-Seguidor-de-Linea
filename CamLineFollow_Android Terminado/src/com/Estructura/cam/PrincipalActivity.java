package com.Estructura.cam;

import android.app.Activity;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.widget.Toast;
// clase principal 
public class PrincipalActivity extends Activity 
{
	public static ProcesarImagen disp;
	public static Dibujar dibuja;
	public static Activity a;
	public final String direccionNxt = "00:16:53:0B:C3:42"; // Direccion del NXT del LIS
	public static Conexion conectar; 
	
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
       
        conectar = new Conexion(direccionNxt);
        conectar.activarBT();
        new Thread()
        {
        	public void run()
        	{
        		conectar.conectarNXT();
        		
        	}
        }.start();
        
        //setup camera display
        a = this;
        disp = new ProcesarImagen(this);
        dibuja = new Dibujar(this);
        setContentView(disp);
        addContentView(dibuja, new LayoutParams(
        		LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        
    }
}