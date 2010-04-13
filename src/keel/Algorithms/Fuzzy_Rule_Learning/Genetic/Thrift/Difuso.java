package keel.Algorithms.Fuzzy_Rule_Learning.Genetic.Thrift;

class Difuso {
	/* This class allows trapezium or triangular-shaped fuzzy set */
	public double x0, x1 ,x2 ,x3, y;
	public String Nombre, Etiqueta;

        public Difuso copia(){
            Difuso copy = new Difuso();
            copy.x0 = this.x0;
            copy.x1 = this.x1;
            copy.x2 = this.x2;
            copy.x3 = this.x3;
            copy.y = this.y;
            copy.Nombre = this.Nombre;
            copy.Etiqueta = this.Etiqueta;
            return copy;
        }
}
