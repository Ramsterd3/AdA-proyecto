package com.analisis.similitud;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SimilitudTest {

    @Test
    public void testDistanciaEuclidianaIguales() {
        DistanciaEuclidiana dist = new DistanciaEuclidiana();
        double[] a = {1.0, 2.0, 3.0};
        double[] b = {1.0, 2.0, 3.0};
        
        double resultado = dist.calcular(a, b);
        assertEquals(0.0, resultado, 0.0001);
    }

    @Test
    public void testDistanciaEuclidiana() {
        DistanciaEuclidiana dist = new DistanciaEuclidiana();
        double[] a = {0.0, 0.0};
        double[] b = {3.0, 4.0};
        
        double resultado = dist.calcular(a, b);
        assertEquals(5.0, resultado, 0.0001);
    }

    @Test
    public void testCorrelacionPearsonPerfecta() {
        CorrelacionPearson corr = new CorrelacionPearson();
        double[] a = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] b = {2.0, 4.0, 6.0, 8.0, 10.0};
        
        double resultado = corr.calcular(a, b);
        assertEquals(1.0, resultado, 0.0001);
    }

    @Test
    public void testCorrelacionPearsonNegativa() {
        CorrelacionPearson corr = new CorrelacionPearson();
        double[] a = {1.0, 2.0, 3.0, 4.0, 5.0};
        double[] b = {5.0, 4.0, 3.0, 2.0, 1.0};
        
        double resultado = corr.calcular(a, b);
        assertEquals(-1.0, resultado, 0.0001);
    }

    @Test
    public void testSimilitudCoseno() {
        SimilitudCoseno cos = new SimilitudCoseno();
        double[] a = {1.0, 0.0};
        double[] b = {1.0, 0.0};
        
        double resultado = cos.calcular(a, b);
        assertEquals(1.0, resultado, 0.0001);
    }

    @Test
    public void testSimilitudCosenoOrtogonal() {
        SimilitudCoseno cos = new SimilitudCoseno();
        double[] a = {1.0, 0.0};
        double[] b = {0.0, 1.0};
        
        double resultado = cos.calcular(a, b);
        assertEquals(0.0, resultado, 0.0001);
    }

    @Test
    public void testDTWIdenticas() {
        DTW dtw = new DTW();
        double[] a = {1.0, 2.0, 3.0};
        double[] b = {1.0, 2.0, 3.0};
        
        double resultado = dtw.calcular(a, b);
        assertEquals(0.0, resultado, 0.0001);
    }

    @Test
    public void testDTWDesplazamiento() {
        DTW dtw = new DTW();
        double[] a = {1.0, 2.0, 3.0};
        double[] b = {0.0, 1.0, 2.0, 3.0};
        
        double resultado = dtw.calcular(a, b);
        assertTrue(resultado >= 0.0);
    }

    @Test
    public void testComplejidades() {
        DistanciaEuclidiana dist = new DistanciaEuclidiana();
        CorrelacionPearson corr = new CorrelacionPearson();
        SimilitudCoseno cos = new SimilitudCoseno();
        DTW dtw = new DTW();

        assertEquals("O(n)", dist.getComplejidad());
        assertEquals("O(n)", corr.getComplejidad());
        assertEquals("O(n)", cos.getComplejidad());
        assertEquals("O(n*m)", dtw.getComplejidad());
    }

    @Test
    public void testSeriesVacias() {
        DistanciaEuclidiana dist = new DistanciaEuclidiana();
        CorrelacionPearson corr = new CorrelacionPearson();
        SimilitudCoseno cos = new SimilitudCoseno();
        DTW dtw = new DTW();

        double[] vacio = {};
        
        assertEquals(0.0, dist.calcular(vacio, vacio), 0.0001);
        assertEquals(0.0, corr.calcular(vacio, vacio), 0.0001);
        assertEquals(0.0, cos.calcular(vacio, vacio), 0.0001);
    }
}