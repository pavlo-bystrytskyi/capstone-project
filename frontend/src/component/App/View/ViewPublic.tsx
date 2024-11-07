import View from "../View.tsx";
import {useTranslation} from "react-i18next";
import ViewConfig from "../../../type/ViewConfig.tsx";

export default function ViewPublic() {

    const {t} = useTranslation();
    const config: ViewConfig = {
        wishlist: {
            url: "/api/wishlist/public/",
            itemIdField: "publicItemIds"
        },
        item: {
            url: "/api/item/public/",
            idField: "publicId"
        }
    }

    return <div className="view-registry-public">
        <h2>{t("view_registry_public")}</h2>
        <View config={config}/>
    </div>
}