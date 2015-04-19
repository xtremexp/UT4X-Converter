/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.xtx.ut4converter.tools;

import javax.vecmath.Matrix4d;
import javax.vecmath.Vector3d;

/**
 * Utility class for geometry operations
 * TODO refactor a bit (some functions from oldy UT3 Converter)
 * @author XtremeXp
 */
public class Geometry {

    /**
     *
     * @param v
     * @return
     */
    public static double[] toVectorDouble(Vector3d v){
        return new double[]{ v.x, v.y, v.z};
    }

    /**
     *
     * @param d
     * @return
     */
    public static Vector3d toVector3d(double[] d){
        return new Vector3d(d[0], d[1], d[2]);
    }
    
    private static double radToDegree(double radian)
    {
        return radian*360D/(2*Math.PI);
    }

    /**
     *
     * @param va
     * @param vb
     * @return
     */
    public static double[] getAngles2(Vector3d va,Vector3d vb)
    {
        double cosx;

        double tmp;
        double tmp2;

        //(2,3,4)
        //cos a = (XaXb+YaYb+ZaZb) / sqrt((Xa²+Ya²+Za²)(Xb²+Yb²+Zb² ))

        tmp = va.x*vb.x+va.y*vb.y+va.z*vb.z;
        tmp2 = Math.sqrt((Math.pow(va.x, 2)+Math.pow(va.y, 2)+Math.pow(va.z, 2))*(Math.pow(vb.x, 2)+Math.pow(vb.y, 2)+Math.pow(vb.z, 2)));
        cosx = tmp/tmp2;

        System.out.println("X: "+cosx);
        System.out.println("X: "+radToDegree(Math.acos(cosx)));

        return new double[]{cosx};
    }

    /**
     * Returns the angles in radians
     * @param va
     * @return 
     */
    public static double[] getAnglesInRadians(Vector3d va)
    {
        double cosx;
        double cosy;
        double cosz;
        double tmp;
        double tmp2;
        
        Vector3d vb=new Vector3d(1D, 0D, 0D);
        //(2,3,4)
        //cos a = (XaXb+YaYb+ZaZb) / sqrt((Xa²+Ya²+Za²)(Xb²+Yb²+Zb² ))

        tmp = va.x*vb.x+va.y*vb.y+va.z*vb.z;
        tmp2 = Math.sqrt((Math.pow(va.x, 2)+Math.pow(va.y, 2)+Math.pow(va.z, 2))*(Math.pow(vb.x, 2)+Math.pow(vb.y, 2)+Math.pow(vb.z, 2)));
        cosx = tmp/tmp2;

        vb=new Vector3d(0D, 1D, 0D);
        tmp = va.x*vb.x+va.y*vb.y+va.z*vb.z;
        tmp2 = Math.sqrt((Math.pow(va.x, 2)+Math.pow(va.y, 2)+Math.pow(va.z, 2))*(Math.pow(vb.x, 2)+Math.pow(vb.y, 2)+Math.pow(vb.z, 2)));
        cosy = tmp/tmp2;

        vb=new Vector3d(0D, 0D, 1D);
        tmp = va.x*vb.x+va.y*vb.y+va.z*vb.z;
        tmp2 = Math.sqrt((Math.pow(va.x, 2)+Math.pow(va.y, 2)+Math.pow(va.z, 2))*(Math.pow(vb.x, 2)+Math.pow(vb.y, 2)+Math.pow(vb.z, 2)));
        cosz = tmp/tmp2;

        //System.out.println("X: "+cosx+" Y: "+cosy+" Z: "+cosz);
        //System.out.println("X: "+radToDegree(Math.acos(cosx))+" Y: "+radToDegree(Math.acos(cosy))+" Z: "+radToDegree(Math.acos(cosz)));

        return new double[]{cosx,cosy,cosz};
    }


    /**
     * Rotates the vector
     * @param v Vector to be rotated
     * @param rotation Rotation vector
     * @return Vector rotated
     */
    public static Vector3d rotate(Vector3d v, Vector3d rotation)
    {
        if(rotation == null){
            return v;
        }
        
        return rotate(v, rotation.x, rotation.y, rotation.z);
    }
    /**
     * Make rotate input vector with Yaw(Z),Pitch(Y) and Roll(X) unreal rotation values
     * in the YXZ UT Editor coordinate system.
     *    +00576.000000,+01088.000000,+00192.000000
     * -> -00192.000000,+00064.000000,+00192.000000
     * @param v
     * @param pitch Pitch in Unreal Value (65536 u.v. = 360°)
     * @param yaw Yaw
     * @param roll 
     * @return  
     */
    public static Vector3d rotate(Vector3d v, double pitch, double yaw, double roll)
    {

        pitch = UE12AngleToDegree(pitch);
        yaw = UE12AngleToDegree(yaw);
        roll = UE12AngleToDegree(roll);

        // TODO only divide by 360 if rotation values comes from Unreal Engine 1/2
        double rot_x = ((roll)/360D)*2D*Math.PI; //Roll=Axis X with Unreal Editor
        double rot_y = (((pitch))/360D)*2D*Math.PI; //Pitch=Axis Y with Unreal Editor
        double rot_z = ((yaw)/360D)*2D*Math.PI; //Yaw=Axis Z with Unreal Editor

        
        double tmp[]= new double[]{v.x,v.y,v.z,1D};
        Matrix4d m4d=getGlobalRotationMatrix(rot_x, rot_y, rot_z);

        tmp = getRot(tmp, m4d);
        v.x = tmp[0];
        v.y = tmp[1];
        v.z = tmp[2];
         

        return v;
    }
    
    /**
     * 
     * @param rot_x
     * @param rot_y
     * @param rot_z
     * @return 
     */
    private static Matrix4d getGlobalRotationMatrix(double rot_x, double rot_y, double rot_z)
    {
        Matrix4d m4d;
        
        // Checked
        Matrix4d m4d_x = new Matrix4d(
                1D, 0D, 0D, 0D,
                0D, Math.cos(rot_x),-Math.sin(rot_x), 0D,
                0D, Math.sin(rot_x),Math.cos(rot_x), 0D,
                0D, 0D, 0D, 1D);

        // Checked
        Matrix4d m4d_y = new Matrix4d(
                Math.cos(rot_y), 0D, Math.sin(rot_y), 0D,
                0D, 1D, 0D, 0D,
                -Math.sin(rot_y), 0,Math.cos(rot_y), 0D,
                0D, 0D, 0D, 1D);

        // Checked
        Matrix4d m4d_z = new Matrix4d(
                Math.cos(rot_z), Math.sin(rot_z), 0D, 0D,
                -Math.sin(rot_z), Math.cos(rot_z), 0D, 0D,
                0D, 0D,1D, 0D,
                0D, 0D, 0D, 1D);
        m4d_x = updateMatrix(m4d_x);
        m4d_y = updateMatrix(m4d_y);
        m4d_z = updateMatrix(m4d_z);

        m4d = m4d_x;
        m4d.mul(m4d_y);
        m4d.mul(m4d_z);

        return m4d;
    }

    
    /**
     *
     * @param v
     * @return
     */
    public static Vector3d updateDoubleZeroes(Vector3d v){
        if(Math.abs(v.x) < 0.001D) v.x = 0d;
        if(Math.abs(v.y) < 0.001D) v.y = 0d;
        if(Math.abs(v.z) < 0.001D) v.z = 0d;
        
        return v;
    }

    /**
     *
     * @param d
     * @return
     */
    public static double[] updateDoubleZeroes(double d[])
    {
        for(int i=0;i<d.length;i++)
        {
            if(Math.abs(d[i])<0.001D)
            {
                d[i]=0D;
            }
        }
        return d;
    }
    /**
     * Replaces low values in matrix by zeroes.
     * @param m4d Input 4x4 matrix
     * @return Filtered matrix.
     */
    private static Matrix4d updateMatrix(Matrix4d m4d)
    {
        double tmp;

        for(int i=0;i<4;i++)
        {
            for(int j=0;j<4;j++)
            {
                tmp = m4d.getElement(i, j);
                if(Math.abs(tmp)<0.01){m4d.setElement(i, j, 0D);}
            }
        }
        return m4d;
    }

    /**
     * Convert an unreal engine 1/2 angle to degree.
     * 65536 Unreal Angle (UE1, 2) = 360°
     * @param angle Unreal Angle from Unreal Engine 1 or 2
     * @return Unreal angle in degrees
     */
    public static double UE12AngleToDegree(double angle)
    {
        int num = (int)(angle/65536D);
        
        if(angle >= 0)
        {
            angle = angle-num*65536D;
        }
        else if(angle < 0)
        {
            angle = angle + num*65536D;
        }

        return (angle/65536D)*360D;
    }

    private static double[] getRot(double[] d2,Matrix4d m4d)
    {
        double d[] = new double []{d2[0],d2[1],d2[2],1D};
        
        double dx = m4d.m00*d[0]+m4d.m10*d[1]+m4d.m20*d[2]+m4d.m30*d[3];
        
        double dy = m4d.m01*d[0]+m4d.m11*d[1]+m4d.m21*d[2]+m4d.m31*d[3];
        double dz = m4d.m02*d[0]+m4d.m12*d[1]+m4d.m22*d[2]+m4d.m32*d[3];

        if(Math.abs(dx)<0.00001D){dx=0D;}
        if(Math.abs(dy)<0.00001D){dy=0D;}
        if(Math.abs(dz)<0.00001D){dz=0D;}
        

        
        return new double[]{dx,dy,dz};
    }
    
    
    /**
     * Transform Permanently a vector like in U1/UT Editor (Brush->Transform Permanently)
     * generally only used for brush data such as vertices, normal and so on.
     * Scale the vertice then make it rotate and scale it again (post scale)
     * @param v Vector to be transformed permanently
     * @param mainScale Main Scale (only available for Unreal Engine 1 UT ...
     * @param rotation Rotation
     * @param postScale Post Scale only available for Unreal Engine 1 UT ...
     * @param isUV if true then vector is TextureU or TextureV data (T3D Brush)
     */
    public static void transformPermanently(Vector3d v, Vector3d mainScale, Vector3d rotation, Vector3d postScale, boolean isUV){
        
        if(mainScale != null){
            
            if(!isUV){
                v.x *= mainScale.x;
                v.y *= mainScale.y;
                v.z *= mainScale.z;
            } else {
                v.x /= mainScale.x;
                v.y /= mainScale.y;
                v.z /= mainScale.z;
            }
            
        }
        
        if(rotation != null){
            Geometry.rotate(v, rotation.x, rotation.y, rotation.z);
        }
        
        if(postScale != null){
            
            if(!isUV){
                v.x *= postScale.x;
                v.y *= postScale.y;
                v.z *= postScale.z;
            } else {
                v.x /= postScale.x;
                v.y /= postScale.y;
                v.z /= postScale.z;
            }
            
        }
        
        // avoid values like 1000.00000001 -> 1000.0
        updateDoubleZeroes(v);
    }

    /**
     * Null-safe operation to sum vectors
     * @param a
     * @param b
     * @return 
     */
    public static Vector3d sum(Vector3d a, Vector3d b){
        
        if(a == null && b == null){
            return new Vector3d(0d, 0d, 0d);
        }
        
        else if(a == null && b != null){
            return new Vector3d(b.x, b.y, b.z);
        }
        
        else if(a != null && b == null){
            return new Vector3d(a.x, a.y, a.z);
        }
        
        else {
            return new Vector3d(a.x + b.x, a.y + b.y, a.z + b.z);
        }
    }
    
    /**
     * Null-safe operation to subtract vectors
     * @param a
     * @param b
     * @return Vector a - Vector b
     */
    public static Vector3d sub(Vector3d a, Vector3d b){
        
        if(a == null && b == null){
            return new Vector3d(0d, 0d, 0d);
        } 
        
        else if(a == null && b != null){
            return new Vector3d(-b.x, -b.y, -b.z);
        }
        
        else if(a != null && b == null){
            return new Vector3d(a.x, a.y, a.z);
        }
        
        else {
            return new Vector3d(a.x - b.x, a.y - b.y, a.z - b.z);
        }
    }
}
