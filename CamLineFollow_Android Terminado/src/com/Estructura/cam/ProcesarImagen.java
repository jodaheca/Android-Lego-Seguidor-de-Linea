package com.Estructura.cam;

import java.io.ByteArrayOutputStream;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.hardware.Camera.Size;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.widget.Toast;
  // clase que procesa la imagen que muestra la camara.
public class ProcesarImagen extends SurfaceView implements Callback 
{
	public SurfaceHolder sh;
	public Camera cam;
	public int ancho;
	public int alto;
	public Bitmap bitmap;
	int indiceMinimo = -1;
	boolean lightOn = false;
	int min = 256;
	// comando que es enviado al NXT
	byte[] dat = { 0x0c, 0x00, (byte) 0x80, 0x04, 0x02, 0x32, 0x07, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00,
            0x0c, 0x00, (byte) 0x80, 0x04, 0x01, 0x32, 0x07, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00 };
    ;
    // Velocidad a la que van los motores
    byte l =  dat[5];
	byte r =  dat[19];
	
	public ProcesarImagen(Context context) 
	{
		super(context);
		
		sh = getHolder();
		sh.addCallback(this);
		sh.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
	}
     // crear superficie 
	public void surfaceCreated(SurfaceHolder holder) 
	{   
		if(cam == null){
			// abrir la camara 
			cam = Camera.open();
		}
		//encender el flash 
		setFlashlight(true);
		try
		{
			cam.setPreviewCallback(new PreviewCallback()
			{
				public void onPreviewFrame(byte[] data, Camera c) 
				{
					final byte[] d = data;
					if(cam != null)
					{
						//Ingresar los datos que muestra la imagen en una matriz de bits 
						Size previewSize = cam.getParameters().getPreviewSize();
						YuvImage yu = new YuvImage(d, ImageFormat.NV21, 
								previewSize.width, previewSize.height, null);
						ByteArrayOutputStream out = new ByteArrayOutputStream();
						yu.compressToJpeg(new Rect(0, 0, previewSize.width, 
								previewSize.height), 0, out);
						Bitmap b = BitmapFactory.decodeByteArray(out.toByteArray(),
								0, out.toByteArray().length);
						//rotar la imagen, porque la cámara vuelve la imagen fuera de 90 grados
						Matrix m = new Matrix();
						m.postRotate(90);
						b = Bitmap.createBitmap(b, 0, 0, previewSize.width,
								previewSize.height, m, true);
						
						//encontrar el punto "más oscuro" con el componente rojo
						int hU = b.getHeight()/3;
					     min = 256;  // blanco
						for(int x = 0; x < b.getWidth(); x++)
						{
							int r = Color.red(b.getPixel(x, hU));
							if(r < min)
							{
								min = r;
								indiceMinimo = x; // punto medio
								
							}
						}
						
						
							// verifica que el punto encontrado sea negro "evita sombras"
							if (min<64){
							// pregunta si la linea esta en el centro
							if (indiceMinimo>237 && indiceMinimo<474){
								// Los motores se deben mover con la misma velocidad
								r = (byte)50;
								l = (byte)50;
							}
							// pregunta si la linea esta a la izquierda
							else if(indiceMinimo<=237){
								// reduce la velocidad del motor izquierdo
								byte r = (byte)50;
								byte l = (byte)25;
								// pregunta si la linea esta en el extremo izquierdo
								if(indiceMinimo==0){
									//detiene el motor de la izquierda
									r = (byte)75;
									l = (byte)0;
								}
								// pregunta si la linea esta a la derecha 
							}else if(indiceMinimo>=474) {
							    // reduce la velocidad del motor de la derecha
								byte r = (byte)25;
								byte l = (byte)50;
								// pregunta si la linea esta en el extremo derecho 
								if(indiceMinimo>710){
									// detiene el motor de la derecha 
									r = (byte)0;
									l = (byte)75;
								}
							}
							}
							
				// verifica que el NXT este conectado
			   	if(PrincipalActivity.conectar.conectado)
							{
	                 //Enviar ordenes al NXT
			   		PrincipalActivity.conectar.escribirMensaje(ande(l, r));
					}
						
						//show the picture on the screen
						bitmap = b;
						}
					}
				});
			}
		
		
		catch (Exception e)
		{
		}
	}
	// datos a ser enviados al NXT
	public byte[] ande(byte l, byte r) {
        byte[] data = { 0x0c, 0x00, (byte) 0x80, 0x04, 0x02, 0x32, 0x07, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00,
                        0x0c, 0x00, (byte) 0x80, 0x04, 0x01, 0x32, 0x07, 0x00, 0x00, 0x20, 0x00, 0x00, 0x00, 0x00 };
        
        //Log.i("NXT", "motors: " + Byte.toString(l) + ", " + Byte.toString(r));
        
        data[5] = l;
        data[19] = r;
        
        return(data);
    }
	// cambiar superficie 
	public void surfaceChanged(SurfaceHolder holder, int format, int w,
			int h) 
	{
		cam.setDisplayOrientation(90);
		
		cam.startPreview();
		ancho = w;
		alto = h;
	}
       // destruir superficie 
	public void surfaceDestroyed(SurfaceHolder holder) 
	{
		cam.setPreviewCallback(null);
		cam.stopPreview();
		cam.release();
		cam = null;
	}
	
	//http://stackoverflow.com/questions/3878294/camera-parameters-flash-mode-torch-replacement-for-android-2-1
	public boolean setFlashlight(boolean isOn)
	{
	    if (cam == null)
	    {
	        return false;
	    }
	    Camera.Parameters params = cam.getParameters();
	    String value;
	    if (isOn) // Si la camara esta encendida 
	    {
	        value = Camera.Parameters.FLASH_MODE_TORCH;
	    }
	    else 
	    {
	        value =  Camera.Parameters.FLASH_MODE_AUTO;
	    }
          // Inicia el procesamiento de la imagen de nuevo
	    try{    
	        params.setFlashMode(value);
	        cam.setParameters(params);

	        String nowMode = cam.getParameters().getFlashMode();

	        if (isOn && nowMode.equals(Camera.Parameters.FLASH_MODE_TORCH))
	        {
	            return true;
	        }
	        if (! isOn && nowMode.equals(Camera.Parameters.FLASH_MODE_AUTO))
	        {
	            return true;
	        }
	        return false;
	    }
	    catch (Exception ex)
	    {
	    }
	    return false;
	}
	

   }

