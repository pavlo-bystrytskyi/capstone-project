export default interface RegistryConfig {
    wishlist: {
        url: string,
        itemIdField: string,
        privateLinkTemplate: string | null,
        publicLinkTemplate: string | null
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