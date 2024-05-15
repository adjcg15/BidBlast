package com.bidblast.api.responses.auctioncategories;

public class UpdatedAuctionCategoryJSONResponse {
    Boolean isError;
    int statusCode;
    String details;

    public UpdatedAuctionCategoryJSONResponse() {}

    public UpdatedAuctionCategoryJSONResponse(Boolean isError, int statusCode, String details) {
        this.isError = isError;
        this.statusCode = statusCode;
        this.details = details;
    }

    public Boolean getError() {
        return isError;
    }

    public void setError(Boolean error) {
        isError = error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
