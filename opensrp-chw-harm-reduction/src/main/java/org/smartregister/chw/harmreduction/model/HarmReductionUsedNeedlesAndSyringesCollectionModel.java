package org.smartregister.chw.harmreduction.model;

public class HarmReductionUsedNeedlesAndSyringesCollectionModel {
    private String collectionId;
    private String collectionDate;
    private String usedNeedlesAndSyringesCollected;

    public HarmReductionUsedNeedlesAndSyringesCollectionModel() {
    }

    public HarmReductionUsedNeedlesAndSyringesCollectionModel(String collectionDate, String usedNeedlesAndSyringesCollected, String collectionId) {
        this.collectionDate = collectionDate;
        this.usedNeedlesAndSyringesCollected = usedNeedlesAndSyringesCollected;
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

    public String getUsedNeedlesAndSyringesCollected() {
        return usedNeedlesAndSyringesCollected;
    }

    public void setUsedNeedlesAndSyringesCollected(String usedNeedlesAndSyringesCollected) {
        this.usedNeedlesAndSyringesCollected = usedNeedlesAndSyringesCollected;
    }
}
