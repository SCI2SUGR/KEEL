package keel.Algorithms.Rule_Learning.REX1.FormulasEntropia;

import java.util.Iterator;

public class Entropia2 implements EntropiaInterface {

	@Override
	public double calculaEntropia(Iterable<Float> probabilidades) {
		double toRet=0;
		double probabilidadActual;
		double logaritmoNatural2 = Math.log(2);

		Iterator<Float> i = probabilidades.iterator();

		while(i.hasNext()){
			probabilidadActual = i.next();
			toRet = toRet + probabilidadActual * Math.log(probabilidadActual)/logaritmoNatural2;
		}

		return -toRet;
	}

	@Override
	public Integer getCodigo() {
		return 2;
	}

}
