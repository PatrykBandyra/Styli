export default class ImagesResponse {
    public content?: Content[];
    public pageable?: Pageable;
    public last?: boolean;
    public totalElements?: number;
    public totalPages?: number;
    public size?: number;
    public number?: number;
    public sort?: Sort;
    public first?: boolean;
    public numberOfElements?: number;
    public empty?: boolean;
}

export class Pageable {
    public sort?: Sort;
    public offset?: number;
    public pageSize?: number;
    public pageNumber?: number;
    public paged?: boolean;
    public unpaged?: boolean;
}

export class Sort {
    public empty?: boolean;
    public sorted?: boolean;
    public unsorted?: boolean;
}

export class Content {
    public id?: number;
    public image?: string;
    public description?: string;
    public username?: any;
}
