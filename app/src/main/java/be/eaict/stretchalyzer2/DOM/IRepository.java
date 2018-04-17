package be.eaict.stretchalyzer2.DOM;

import java.util.Date;
import java.util.List;

/**
 * Created by Kevin-Laptop on 1/03/2018.
 */

public interface IRepository {

    //region GETTERS

    List<fxDatapoint> getFxDataPoints();

    List<fxDatapoint> getFxDataPoints(Date datum);

    //endregion

    //region SETTERS

    void createFxDataPoint(fxDatapoint fxdatapoint);
    void updateFxDataPoint(fxDatapoint fxdataPoint);
    //void createOrUpdateFxDataPoint(fxDatapoint fxdataPoint);

    //endregion
}
