/***********************************************************************

	This file is part of KEEL-software, the Data Mining tool for regression, 
	classification, clustering, pattern mining and so on.

	Copyright (C) 2004-2010
	
	F. Herrera (herrera@decsai.ugr.es)
    L. Sánchez (luciano@uniovi.es)
    J. Alcalá-Fdez (jalcala@decsai.ugr.es)
    S. García (sglopez@ujaen.es)
    A. Fernández (alberto.fernandez@ujaen.es)
    J. Luengo (julianlm@decsai.ugr.es)

	This program is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	This program is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with this program.  If not, see http://www.gnu.org/licenses/
  
**********************************************************************/

/**
 * <p>
 * @author Written by Cristobal J. Carmona (University of Jaen) 10/07/2010
 * @version 1.0
 * @since JDK1.6
 * </p>
 */

package keel.Algorithms.Subgroup_Discovery.SDMap.SDMap;

public class QualitySubgroup {

    /**
     * <p>
     * Class with the quality measures need for obtaining the best subgroups
     * </p>
     */


    private double tp;
    private double fp;
    private double TPm;
    private double FPm;
    private double q;

    /**
     * <p>
     * It creates a new object empty
     * </p>
     */
    public QualitySubgroup(){

    }

    /**
     * <p>
     * It creates a new object with the values of the quality measures
     * </p>
     * @param _tp        Value of the tp measure
     * @param _fp        Value of the fp measure
     * @param _TPm        Value of the TP missing measure
     * @param _FPm        Value of the FP missing measure
     * @param _q        Value of the quality measure
     */
    public QualitySubgroup(double _tp, double _fp, double _TPm, double _FPm, double _q){

        tp = _tp;
        fp = _fp;
        TPm = _TPm;
        FPm = _FPm;
        q = _q;

    }

    /**
     * <p>
     * This function copies in this object the auxiliar
     * </p>
     * @param aux           Auxiliar object QualitySubgroup
     */
    public void copy(QualitySubgroup aux){
        set_tp(aux.get_tp());
        set_fp(aux.get_fp());
        set_TPm(aux.get_TPm());
        set_FPm(aux.get_FPm());
        set_q(aux.get_q());
    }

    /**
     * <p>
     * Gets the value of the tp quality measure
     * </p>
     * @return                  Value of the tp
     */
    public double get_tp(){
        return tp;
    }

    /**
     * <p>
     * Sets the value for the tp quality measure
     * </p>
     * @param val            Value of the tp
     */
    public void set_tp(double val){
        tp = val;
    }

    /**
     * <p>
     * Gets the value of the fp quality measure
     * </p>
     * @return                  Value of the fp
     */
    public double get_fp(){
        return fp;
    }

    /**
     * <p>
     * Sets the value for the fp quality measure
     * </p>
     * @param val            Value of the fp
     */
    public void set_fp(double val){
        fp = val;
    }

    /**
     * <p>
     * Gets the value of the TP missing quality measure
     * </p>
     * @return                  Value of the TPm
     */
    public double get_TPm(){
        return TPm;
    }

    /**
     * <p>
     * Sets the value for the TP missing quality measure
     * </p>
     * @param val            Value of the TPm
     */
    public void set_TPm(double val){
        TPm = val;
    }

    /**
     * <p>
     * Gets the value of the FP missing quality measure
     * </p>
     * @return                  Value of the FPm
     */
    public double get_FPm(){
        return FPm;
    }

    /**
     * <p>
     * Sets the value for the FP missing quality measure
     * </p>
     * @param val            Value of the FPm
     */
    public void set_FPm(double val){
        FPm = val;
    }

    /**
     * <p>
     * Gets the value of the q quality measure
     * </p>
     * @return                  Value of the q
     */
    public double get_q(){
        return q;
    }

    /**
     * <p>
     * Sets the value for the q quality measure
     * </p>
     * @param val            Value of the q
     */
    public void set_q(double val){
        q = val;
    }

}
