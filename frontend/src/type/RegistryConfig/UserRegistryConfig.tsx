import RegistryConfig from "../RegistryConfig.tsx";

const userRegistryConfig: RegistryConfig = {
    wishlist: {
        url: "/api/user/wishlist",
        itemIdField: "privateItemIds",
        publicLinkTemplate: window.location.origin +  "/show-public/",
        privateLinkTemplate: window.location.origin +  "/show-user/",
    },
    item: {
        url: "/api/user/item",
        idField: "privateId"
    },
    access: {
        wishlist: {
            edit: {
                allowed: true,
                url: "/edit-user"
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

export default userRegistryConfig;