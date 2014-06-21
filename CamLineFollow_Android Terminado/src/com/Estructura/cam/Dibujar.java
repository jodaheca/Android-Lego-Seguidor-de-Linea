package com.Estructura.cam;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Paint.Style;
import android.view.View;
 // clase que muestra la imagen procesada por pantalla
public class Dibujar extends View 
{	
	public Dibujar(Context c)
	{
		super(c);
	}
	     
	public void dibuja(Canvas c)
	{
		Paint p = new Paint();
		p.setColor(Color.RED);
		Paint q = new Paint();
		q.setTextSize(30);
		q.setTypeface(Typeface.SERIF);
		
		
		//dibujar el contenido de la camara
		if(PrincipalActivity.disp.bitmap!= null)
			c.drawBitmap(PrincipalActivity.disp.bitmap, 0, 0, p);

		//  dibujar el punto mas oscuro y el valor del color negro
		int hU = getHeight()/3;
		int indice = PrincipalActivity.disp.indiceMinimo;
		int negro = PrincipalActivity.disp.min;
		p.setColor(Color.RED);
		p.setStyle(Style.STROKE);
		p.setStrokeWidth(5);
		c.drawRect(indice, hU-90, indice+1, hU+10, p);
		c.drawText("Pos en X : "+indice, 0, 1000, q);
		c.drawText("El punto mas negro es de color: "+negro, 0, 1030, q);
		c.drawText("Motor Izquierdo: "+PrincipalActivity.disp.l+"  Motor Derecho "+PrincipalActivity.disp.r, 0, 1060, q);
		if(negro>63){ // cuando no encuentre la linea
			q.setColor(Color.RED);
			c.drawText("       no encuentro la linea!!", 0, 1090, q);
		}

	}
}
