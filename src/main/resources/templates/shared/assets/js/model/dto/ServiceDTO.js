export class ServiceDTO {
    constructor(productEntityName, minPrice, maxPrice, minStarRating, sortFieldName, sortDirection) {
        this.productEntityName = productEntityName;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.minStarRating = minStarRating;
        this.sortFieldName = sortFieldName;
        this.sortDirection = sortDirection;
    }

    static fromObject(obj) {
        const serviceDTO = new ServiceDTO();
        if ("productEntityName" in obj) {
            serviceDTO.productEntityName = obj.productEntityName;
        }
        if ("minPrice" in obj) {
            serviceDTO.minPrice = obj.minPrice;
        }
        if ("maxPrice" in obj) {
            serviceDTO.maxPrice = obj.maxPrice;
        }
        if ("minStarRating" in obj) {
            serviceDTO.minStarRating = obj.minStarRating;
        }
        if ("sortFieldName" in obj) {
            serviceDTO.sortFieldName = obj.sortFieldName;
        }
        if ("sortDirection" in obj) {
            serviceDTO.sortDirection = obj.sortDirection;
        }
        return serviceDTO;
    }
}