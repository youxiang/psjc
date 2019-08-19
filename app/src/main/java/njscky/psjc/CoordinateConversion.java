package njscky.psjc;

/**
 * Created by Administrator on 2015/7/12.
 */
public class CoordinateConversion {
    public static String ConvertXY(double L, double B) {
        final double PI = 3.14159265358979;
        final double a = 6378137.0;
        final double e2 = 0.0066943799013;
        final double px = -3399990.0;
        final double py = -365234.0;
        final double pa = 359.59580039;
        final double pm = -2.98E-06;
        final double C3 = 0.697548;
        final double C2 = 133.959889;
        final double C1 = 32009.81853;
        final double C0 = 6367449.145823;
        final double m_L0 = (118.0 + 50.0 / 60) * PI / 180.0;
        final double m_a = ((int) pa + ((int) (pa * 100) - (int) (pa) * 100) / 60.0 +
                (pa * 10000 - (int) (pa * 100) * 100) / 3600.0) * PI / 180.0;
        //============================
        //d.mmss格式

        if (B > 0 && L > 0) {
            double m_B = B * PI / 180.0;
            double m_L = L * PI / 180.0;
            //============================
            //d.dddd格式

            //    m_B = SX * PI / 180#
            //    m_L = SY * PI / 180#
            double l = m_L - m_L0;

            double t = Math.tan(m_B);
            double m0 = l * Math.cos(m_B);
            double n2 = e2 / (1.0 - e2) * Math.cos(m_B);
            double X0B = C0 * m_B -
                    Math.cos(m_B) * (C1 * Math.sin(m_B) + C2 * Math.pow(Math.sin(m_B), 3.0) + C3 * Math.pow(Math.sin(m_B), 5));
            double N = a / Math.sqrt(1 - e2 * Math.sin(m_B) * Math.sin(m_B));
            double m_X = X0B + 0.5 * N * t * m0 * m0 + (5.0 - t * t + 9.0 * n2 + 4.0 * Math.pow(n2, 2.0)) * N * t * Math.pow(m0, 4.0) / 24.0 +
                    (61.0 - 58.0 * t * t + Math.pow(t, 4.0) + 270.0 * n2 - 330.0 * n2 * t * t) * N * t * Math.pow(m0, 4.0) / 720.0;
            double m_Y = 500000.0 + N * m0 + (1.0 - t * t + n2) * N * Math.pow(m0, 3.0) / 6.0 +
                    (5.0 - 18.0 * t * t + Math.pow(t, 4.0) + 14.0 * n2 - 58.0 * n2 * t * t) * N * Math.pow(m0, 5.0) / 120.0;

            //============================
            //地方坐标计算格式

            double DX = px + (1.0 + pm) * (Math.cos(m_a) * m_X - Math.sin(m_a) * m_Y);
            double DY = py + (1.0 + pm) * (Math.sin(m_a) * m_X + Math.cos(m_a) * m_Y);

            return (String.valueOf(DY) + ";" + String.valueOf(DX));
        } else {
            return ("0;0");
        }
    }

    public static double[] convert(double L, double B) {
        final double PI = 3.14159265358979;
        final double a = 6378137.0;
        final double e2 = 0.0066943799013;
        final double px = -3399990.0;
        final double py = -365234.0;
        final double pa = 359.59580039;
        final double pm = -2.98E-06;
        final double C3 = 0.697548;
        final double C2 = 133.959889;
        final double C1 = 32009.81853;
        final double C0 = 6367449.145823;
        final double m_L0 = (118.0 + 50.0 / 60) * PI / 180.0;
        final double m_a = ((int) pa + ((int) (pa * 100) - (int) (pa) * 100) / 60.0 +
                (pa * 10000 - (int) (pa * 100) * 100) / 3600.0) * PI / 180.0;
        //============================
        //d.mmss格式

        if (B > 0 && L > 0) {
            double m_B = B * PI / 180.0;
            double m_L = L * PI / 180.0;
            //============================
            //d.dddd格式

            //    m_B = SX * PI / 180#
            //    m_L = SY * PI / 180#
            double l = m_L - m_L0;

            double t = Math.tan(m_B);
            double m0 = l * Math.cos(m_B);
            double n2 = e2 / (1.0 - e2) * Math.cos(m_B);
            double X0B = C0 * m_B -
                    Math.cos(m_B) * (C1 * Math.sin(m_B) + C2 * Math.pow(Math.sin(m_B), 3.0) + C3 * Math.pow(Math.sin(m_B), 5));
            double N = a / Math.sqrt(1 - e2 * Math.sin(m_B) * Math.sin(m_B));
            double m_X = X0B + 0.5 * N * t * m0 * m0 + (5.0 - t * t + 9.0 * n2 + 4.0 * Math.pow(n2, 2.0)) * N * t * Math.pow(m0, 4.0) / 24.0 +
                    (61.0 - 58.0 * t * t + Math.pow(t, 4.0) + 270.0 * n2 - 330.0 * n2 * t * t) * N * t * Math.pow(m0, 4.0) / 720.0;
            double m_Y = 500000.0 + N * m0 + (1.0 - t * t + n2) * N * Math.pow(m0, 3.0) / 6.0 +
                    (5.0 - 18.0 * t * t + Math.pow(t, 4.0) + 14.0 * n2 - 58.0 * n2 * t * t) * N * Math.pow(m0, 5.0) / 120.0;

            //============================
            //地方坐标计算格式

            double DX = px + (1.0 + pm) * (Math.cos(m_a) * m_X - Math.sin(m_a) * m_Y);
            double DY = py + (1.0 + pm) * (Math.sin(m_a) * m_X + Math.cos(m_a) * m_Y);

            return new double[]{DX, DY};
        } else {
            return new double[]{0, 0};
        }
    }
}
