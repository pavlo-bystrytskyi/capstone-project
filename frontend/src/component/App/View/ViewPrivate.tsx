import View from "../View.tsx";
import {useTranslation} from "react-i18next";
import ViewConfig from "../../../type/ViewConfig.tsx";

export default function ViewPrivate() {

    const {t} = useTranslation();
    const config: ViewConfig = {
        wishlist: {
            url: "/api/wishlist/",
            itemIdField: "privateItemIds"
        },
        item: {
            url: "/api/item/",
            idField: "privateId"
        }
    }

    return <div className="view-registry-private">
        <h2>{t("view_registry_private")}</h2>
        <View config={config}/>
    </div>
}