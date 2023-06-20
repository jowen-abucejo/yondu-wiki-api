package com.yondu.knowledgebase.DTO.save;

public class SaveStatusDTO {
    private boolean isSaved;
    private Long saveId;

    public SaveStatusDTO(boolean isSaved, Long saveId) {
        this.isSaved = isSaved;
        this.saveId = saveId;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public Long getSaveId() {
        return saveId;
    }

    public void setSaveId(Long saveId) {
        this.saveId = saveId;
    }
}
