import RegistryConfig from "../RegistryConfig.tsx";

const privateRegistryConfig: RegistryConfig = {
    wishlist: {
        url: "/api/guest/wishlist",
        itemIdField: "privateItemIds",
        publicLinkTemplate: window.location.origin +  "/show-public/",
        privateLinkTemplate: window.location.origin +  "/show-private/",
    },
    item: {
        url: "/api/guest/item",
        idField: "privateId"
    },
    access: {
        wishlist: {
            edit: {
                allowed: true,
                url: "/edit-guest"
            }
        },
        item: {
            status: {
                edit: {
                    allowed: false
                }
            }
        }
    }
}

export default privateRegistryConfig;