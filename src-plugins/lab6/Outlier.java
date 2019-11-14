package lab6;

import java.util.ArrayList;
import java.util.List;

public class Outlier
{
    private static double getMean(ArrayList<Double> values)
    {
        double sum = 0;
        for (double value : values)
        {
            sum += value;
        }

        return sum / values.size();
    }

    private static double getVariance(ArrayList<Double> values)
    {
        double mean = getMean(values);
        double temp = 0;

        for (double a : values)
        {
            temp += (a - mean) * (a - mean);
        }

        return temp / (values.size() - 1);
    }

    private static double getStdDev(ArrayList<Double> values)
    {
        return Math.sqrt(getVariance(values));
    }

    public static ArrayList<Double> eliminateOutliers(ArrayList<Double> values, double scaleOfElimination)
    {
        double mean = getMean(values);
        double stdDev = getStdDev(values);

        final ArrayList<Double> newList = new ArrayList<>();

        for (double value : values)
        {
            boolean isLessThanLowerBound = value < mean - stdDev * scaleOfElimination;
            boolean isGreaterThanUpperBound = value > mean + stdDev * scaleOfElimination;
            boolean isOutOfBounds = isLessThanLowerBound || isGreaterThanUpperBound;

            if (!isOutOfBounds)
            {
                newList.add(value);
            }
        }

        int countOfOutliers = values.size() - newList.size();
        if (countOfOutliers == 0)
        {
            return values;
        }

        return eliminateOutliers(newList, scaleOfElimination);
    }
}
