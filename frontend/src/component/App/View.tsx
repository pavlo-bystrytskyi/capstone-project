import {useParams} from "react-router-dom";
import ItemContainer from "././View/ItemContainer.tsx";
import {useTranslation} from "react-i18next";
import {useEffect, useState} from "react";
import axios from "axios";
import Registry from "../../type/Registry.tsx";
import {emptyRegistry} from "../../type/EmptyRegistry.tsx";
import ViewConfig from "../../type/ViewConfig.tsx";

export default function View({config}: { config: ViewConfig }) {
    const {t} = useTranslation();
    const params = useParams();
    const id: string | undefined = params.id;
    const [wishlist, setWishlist] = useState<Registry>(emptyRegistry);
    const loadWishlist = function () {
        axios.get<Registry>(config.wishlist.url + id).then(
            (response) => setWishlist(response.data)
        ).catch(error => {
            console.error('Error fetching data:', error);
        });
    }
    useEffect(
        loadWishlist,
        [id]
    );
    const itemIdField = config.wishlist.itemIdField as keyof Registry;

    return <>
        <form className="registry-form">
            <label htmlFor="title">{t("registry_name")}</label>
            <input type="text" name="title" disabled={true} value={wishlist.title}/>
            <label htmlFor="description">{t("registry_description")}</label>
            <input type="text" name="description" disabled={true} value={wishlist.description}/>
        </form>
        {wishlist?.[itemIdField] &&
            <ItemContainer itemIdList={wishlist[itemIdField] as string[]} config={config}/>}
    </>
}
