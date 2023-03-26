package my.home.jnom.service;

import my.home.jnom.dao.jnom.AdmBoundaryDAO;
import my.home.jnom.dao.osm.OsmPolygonDAO;
import my.home.jnom.entity.AdmBoundaryEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;


@Service
public class AdmBoundaryDataService {
    @Autowired
    private OsmPolygonDAO osmPolygonDAO;

    @Autowired
    private AdmBoundaryDAO admBoundaryDAO;

    public void importAdmBoundaries() {
        List<AdmBoundaryEntity> boundaries = findAdmBoundaries(3, 10);
        enrichAndSaveAdmBoundaries(boundaries);
    }

    private List<AdmBoundaryEntity> findAdmBoundaries(Integer minAdminLevel, Integer maxAdminLevel) {
        List<AdmBoundaryEntity> result = new ArrayList<>();
        IntStream.range(minAdminLevel, maxAdminLevel)
                .forEach(adminLevel -> result.addAll(osmPolygonDAO.findAdmBoundaries(adminLevel)));
        return result;
    }

    private void enrichAndSaveAdmBoundaries(List<AdmBoundaryEntity> boundaries) {
        boundaries.forEach(admBoundary -> {
            admBoundary.setParentOsmId(osmPolygonDAO.findParentForAdmBoundary(admBoundary.getOsmId()).orElse(null));
            admBoundaryDAO.insertOfUpdate(admBoundary);
        });
    }

}
