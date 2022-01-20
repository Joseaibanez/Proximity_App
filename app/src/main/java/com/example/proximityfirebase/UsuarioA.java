package com.example.proximityfirebase;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

public class UsuarioA {

    Circle perimetro;
    ArrayList<LatLng> poligono;

    public UsuarioA() {}

    public void drawPolygon(GoogleMap mapa) {
        ArrayList<LatLng> polygonList = new ArrayList<>();
        LatLng point1 = new LatLng(42.621510945637574, -5.618901350675462);
        LatLng point2 = new LatLng(42.62153705584088, -5.618843056519884);
        LatLng point3 = new LatLng(42.621477997032045, -5.6188067282780025);
        LatLng point4 = new LatLng(42.62144318339215, -5.618873470861924);
        polygonList.add(point1);
        polygonList.add(point2);
        polygonList.add(point3);
        polygonList.add(point4);
        Polygon polygon1 = mapa.addPolygon(new PolygonOptions()
                .clickable(false)
                .add(
                        point1,
                        point2,
                        point3,
                        point4));
        polygon1.setTag("alpha");
        poligono = polygonList;
    }

    public ArrayList<LatLng> getPoligono() {return poligono;}

    public Circle getCircle() {
        return perimetro;
    }

    public void drawCircle(GoogleMap mapa, LatLng coordenates, int radio) {
        CircleOptions cOptions = new CircleOptions();
        cOptions.center(coordenates);
        cOptions.radius(radio);
        cOptions.strokeColor(Color.BLACK);
        cOptions.fillColor(Color.parseColor("#2271cce7"));
        cOptions.strokeWidth(2);
        mapa.addMarker(new MarkerOptions().position(coordenates).title("Usuario A"));
        perimetro = mapa.addCircle(cOptions);
    }
}