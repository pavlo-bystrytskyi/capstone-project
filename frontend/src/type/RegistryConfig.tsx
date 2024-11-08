export default interface RegistryConfig {
    wishlist: {
        url: string,
        itemIdField: string
    },
    item: {
        url: string,
        idField: string
    },
    access: {
        wishlist: {
            edit: boolean
        },
        item: {
            status: {
                edit: boolean
            }
        }
    }
}