package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.COR_GA;

class Difuso {
	/* This class allows trapezium or triangular-shaped fuzzy set */
	public double x0, x1 ,x2 ,x3, y;
	public String Nombre, Etiqueta;

        public Difuso copia(){
            Difuso copia = new Difuso();
            copia.x0 = this.x0;
            copia.x1 = this.x1;
            copia.x2 = this.x2;
            copia.x3 = this.x3;
            copia.y = this.y;
            copia.Nombre = this.Nombre;
            copia.Etiqueta = this.Etiqueta;
            return copia;
        }
}
