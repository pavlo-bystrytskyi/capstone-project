import RegistryConfig from "../RegistryConfig.tsx";

const publicRegistryConfig: RegistryConfig = {
    wishlist: {
        url: "/api/guest/wishlist/public",
        itemIdField: "publicItemIds",
        publicLinkTemplate: null,
        privateLinkTemplate: null,
    },
    item: {
        url: "/api/guest/item/public",
        idField: "publicId"
    },
    access: {
        wishlist: {
            edit: {
                allowed: false,
                url: "/"
            }
        },
        item: {
            status: {
                edit: {
                    allowed: true
                }
            }
        }
    }
}

export default publicRegistryConfig;