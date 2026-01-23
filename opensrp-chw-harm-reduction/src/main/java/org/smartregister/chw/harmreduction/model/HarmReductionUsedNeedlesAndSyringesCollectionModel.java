package org.smartregister.chw.harmreduction.model;

public class HarmReductionUsedNeedlesAndSyringesCollectionModel {
    private String collectionId;
    private String collectionDate;
    private String totalSafetyBoxesCollected;

    public HarmReductionUsedNeedlesAndSyringesCollectionModel() {
    }

    public HarmReductionUsedNeedlesAndSyringesCollectionModel(String collectionDate, String totalSafetyBoxesCollected, String collectionId) {
        this.collectionDate = collectionDate;
        this.totalSafetyBoxesCollected = totalSafetyBoxesCollected;
        this.collectionId = collectionId;
    }

    public String getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(String collectionId) {
        this.collectionId = collectionId;
    }

    public String getCollectionDate() {
        return collectionDate;
    }

    public void setCollectionDate(String collectionDate) {
        this.collectionDate = collectionDate;
    }

    public String getTotalSafetyBoxesCollected() {
        return totalSafetyBoxesCollected;
    }

    public void setTotalSafetyBoxesCollected(String totalSafetyBoxesCollected) {
        this.totalSafetyBoxesCollected = totalSafetyBoxesCollected;
    }
}
