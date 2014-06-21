package com.Estructura.cam;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.UUID;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

public class Conexion
{
	public static BluetoothAdapter bt;
	public BluetoothSocket socket;
	public String direccionNxt;
	public boolean conectado = false;
	
	//esta clase facilita todas las cosas del Bluetooth
	public Conexion(String a)
	{
		direccionNxt = a;
	}
	
	//habilitar adaptador de Bluetooth
	public void activarBT()
	{
		bt = BluetoothAdapter.getDefaultAdapter();
		
        if(bt.isEnabled()==false)
        {
            bt.enable();
            
            while(!(bt.isEnabled())) //esperar por Bluetooth
            {}						// terminar de conectar
        }
	}
	
	//conectar el NXT con el smartphone
	public boolean conectarNXT()
	{
		BluetoothDevice nxtt = bt.getRemoteDevice(direccionNxt);
		
		try 
		{
		    socket = nxtt.createRfcommSocketToServiceRecord(UUID
		            .fromString("00001101-0000-1000-8000-00805F9B34FB"));
		    socket.connect();
		
		    conectado = true;
		    
		    
		} 
		catch (IOException e) 
		{
		    conectado = false;
		}
			
		return conectado;
    }
	
	//Mandar las instrucciones de movimiento al NXT
	public void escribirMensaje(byte[] msg)
	{
		if(socket!=null)
    	{
        	try 
        	{
        		OutputStream out = socket.getOutputStream();
        		out.write(msg);
        		out.flush();
        		 
        		
        	} 
        	catch (IOException e) 
        	{
        	}
    	}
		else
    	{
    	}
	}
	
}
