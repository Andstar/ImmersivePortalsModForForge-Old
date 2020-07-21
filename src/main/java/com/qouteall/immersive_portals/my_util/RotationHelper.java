package com.qouteall.immersive_portals.my_util;

import com.qouteall.immersive_portals.Helper;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;

public class RotationHelper {
    //NOTE this will mutate a and return a
    public static Quaternion quaternionNumAdd(Quaternion a, Quaternion b) {
        a.set(
            a.getX() + b.getX(),
            a.getY() + b.getY(),
            a.getZ() + b.getZ(),
            a.getW() + b.getW()
        );
        return a;
    }
    
    //NOTE this will mutate a and reutrn a
    public static Quaternion quaternionScale(Quaternion a, float scale) {
        a.set(
            a.getX() * scale,
            a.getY() * scale,
            a.getZ() * scale,
            a.getW() * scale
        );
        return a;
    }
    
    //a quaternion is a 4d vector on 4d sphere
    //this method may mutate argument but will not change rotation
    public static Quaternion interpolateQuaternion(
        Quaternion a,
        Quaternion b,
        float t
    ) {
        a.normalize();
        b.normalize();
        
        double dot = dotProduct4d(a, b);
        
        if (dot < 0.0f) {
            a.multiply(-1);
            dot = -dot;
        }
        
        double DOT_THRESHOLD = 0.9995;
        if (dot > DOT_THRESHOLD) {
            // If the inputs are too close for comfort, linearly interpolate
            // and normalize the result.
            
            Quaternion result = quaternionNumAdd(
                quaternionScale(a.copy(), 1 - t),
                quaternionScale(b.copy(), t)
            );
            result.normalize();
            return result;
        }
        
        double theta_0 = Math.acos(dot);
        double theta = theta_0 * t;
        double sin_theta = Math.sin(theta);
        double sin_theta_0 = Math.sin(theta_0);
        
        double s0 = Math.cos(theta) - dot * sin_theta / sin_theta_0;
        double s1 = sin_theta / sin_theta_0;
        
        return quaternionNumAdd(
            quaternionScale(a.copy(), (float) s0),
            quaternionScale(b.copy(), (float) s1)
        );
    }
    
    public static double dotProduct4d(Quaternion a, Quaternion b) {
        return a.getX() * b.getX() +
            a.getY() * b.getY() +
            a.getZ() * b.getZ() +
            a.getW() * b.getW();
    }
    
    public static boolean isClose(Quaternion a, Quaternion b, float valve) {
        a.normalize();
        b.normalize();
        if (a.getW() * b.getW() < 0) {
            a.multiply(-1);
        }
        float da = a.getX() - b.getX();
        float db = a.getY() - b.getY();
        float dc = a.getZ() - b.getZ();
        float dd = a.getW() - b.getW();
        return da * da + db * db + dc * dc + dd * dd < valve;
    }
    
    public static Vector3d getRotated(Quaternion rotation, Vector3d vec) {
        Vector3f vector3f = new Vector3f(vec);
        vector3f.transform(rotation);
        return new Vector3d(vector3f);
    }
    
    public static Quaternion ortholize(Quaternion quaternion) {
        if (quaternion.getW() < 0) {
            quaternion.multiply(-1);
        }
        return quaternion;
    }
    
    public static Vector3d getRotatingAxis(Quaternion quaternion) {
        return new Vector3d(
            quaternion.getX(),
            quaternion.getY(),
            quaternion.getZ()
        ).normalize();
    }
    
    //naive interpolation is better?
    //not better
    public static Quaternion interpolateQuaternionNaive(
        Quaternion a,
        Quaternion b,
        float t
    ) {
        return Helper.makeIntoExpression(
            new Quaternion(
                MathHelper.lerp(t, a.getX(), b.getX()),
                MathHelper.lerp(t, a.getY(), b.getY()),
                MathHelper.lerp(t, a.getZ(), b.getZ()),
                MathHelper.lerp(t, a.getW(), b.getW())
            ),
            Quaternion::normalize
        );
    }
    
    
    public static double getXYRotationEigenValue(Quaternion quaternion) {
        return (quaternion.getX() * quaternion.getY()) - (quaternion.getZ() * quaternion.getW());
    }
    
    public static Quaternion getCameraRotation(float pitch, float yaw) {
        Quaternion cameraRotation = Vector3f.XP.rotationDegrees(pitch);
        cameraRotation.multiply(
            Vector3f.YP.rotationDegrees(yaw + 180.0F)
        );
        return cameraRotation;
    }
    
    /**
     * p = pitch/2
     * y = yaw/2
     * qx=-sinp siny, qy=cosp cosy, qz=sinp cosy, qw=-cosp siny
     * the pitch may be illegal (-180 to 180)
     * the result is radians
     * if the input is not a valid camera rotation, it may still work?
     */
    public static Tuple<Double, Double> getPitchYawFromRotation(Quaternion quaternion) {
        float x = quaternion.getX();
        float y = quaternion.getY();
        float z = quaternion.getZ();
        float w = quaternion.getW();
        
        double cosYaw = 2 * (y * y + z * z) - 1;
        double sinYaw = -(x * z + y * w);
        
        double cosPitch = 1 - 2 * (x * x + z * z);
        double sinPitch = x * w + y * z;
        
        return new Tuple<>(
            Math.atan2(sinPitch, cosPitch),
            Math.atan2(sinYaw, cosYaw)
        );
    }
}
