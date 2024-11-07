export default interface ViewConfig {
    wishlist: {
        url: string,
        itemIdField: string
    },
    item: {
        url: string
        idField: string
    },
    access: {
        item: {
            status: {
                edit: boolean
            }
        }
    }
}