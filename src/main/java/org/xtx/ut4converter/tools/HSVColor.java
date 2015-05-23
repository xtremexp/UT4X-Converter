/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.xtx.ut4converter.tools;

/**
 *
 * @author XtremeXp
 */
public class HSVColor {
    
    /**
    * Hue, Saturatio, Value, Alpha
    */
   public float H, S, V, A = 1.f;

   /**
    *
    */
   public HSVColor(){

   }

   /**
    *
    * @param H
    * @param S
    * @param V
    */
   public HSVColor(float H, float S, float V) {
       this.H = H;
       this.S = S;
       this.V = V;
   }
   
   /**
    * Converts to RGB color
    * @return 
    */
   public RGBColor toRGBColor(){
       return ImageUtils.HSVToLinearRGB(this);
   }
   
   public static HSVColor getDefaultUE12Color(){
       return new HSVColor(0, 255, 64);
   }
}
