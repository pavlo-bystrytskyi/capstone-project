import {useParams} from "react-router-dom";
import ItemContainer from "./ViewPublic/ItemContainer.tsx";
import {useTranslation} from "react-i18next";
import {useEffect, useState} from "react";
import axios from "axios";
import Registry from "../../type/Registry.tsx";
import {emptyRegistry} from "../../type/EmptyRegistry.tsx";

export default function ViewPublic() {
    const {t} = useTranslation();
    const params = useParams();
    const publicId: string | undefined = params.id;
    const [wishlist, setWishlist] = useState<Registry>(emptyRegistry);

    const loadWishlist = function () {
        axios.get<Registry>("/api/wishlist/public/" + publicId).then(
            (response) => setWishlist(response.data)
        ).catch(error => {
            console.error('Error fetching data:', error);
        });
    }
    useEffect(
        loadWishlist,
        [publicId]
    );

    return <div className="view-registry-public">
        <h2>{t("view_registry_public")}</h2>
        <form className="registry-form">
            <label htmlFor="title">{t("registry_name")}</label>
            <input type="text" name="title" disabled={true} value={wishlist.title}/>
            <label htmlFor="description">{t("registry_description")}</label>
            <input type="text" name="description" disabled={true} value={wishlist.description}/>
        </form>
        {wishlist?.publicItemIds && <ItemContainer itemIdList={wishlist.publicItemIds}/>}
    </div>
}