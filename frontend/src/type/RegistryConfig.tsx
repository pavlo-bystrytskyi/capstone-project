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
            edit: {
                allowed: boolean,
                url: string
            }
        },
        item: {
            status: {
                edit: {
                    allowed: boolean
                }
            }
        }
    }
}