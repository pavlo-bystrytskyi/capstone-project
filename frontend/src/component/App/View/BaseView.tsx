import {useNavigate, useParams} from "react-router-dom";
import ItemContainer from "./ItemContainer.tsx";
import {useTranslation} from "react-i18next";
import {useEffect, useState} from "react";
import axios from "axios";
import Registry from "../../../type/Registry.tsx";
import {emptyRegistry} from "../../../type/EmptyRegistry.tsx";
import RegistryConfig from "../../../type/RegistryConfig.tsx";

export default function BaseView({config}: { readonly config: RegistryConfig }) {
    const {t} = useTranslation();
    const params = useParams();
    const id: string | undefined = params.id;
    const navigate = useNavigate();
    const [wishlist, setWishlist] = useState<Registry>(emptyRegistry);
    const loadWishlist = function () {
        axios.get<Registry>(`${config.wishlist.url}/${id}`).then(
            (response) => setWishlist(response.data)
        ).catch(error => {
            console.error('Error fetching data:', error);
        });
    }
    useEffect(
        loadWishlist,
        [id]
    );
    const openEditPage = function () {
        navigate(`${config.access.wishlist.edit.url}/${id}`);
    }

    return <>
        <form className="registry-form" onSubmit={openEditPage}>
            <label htmlFor="title">{t("registry_name")}</label>
            <input type="text" name="title" disabled={true} value={wishlist.title}/>
            <label htmlFor="description">{t("registry_description")}</label>
            <input type="text" name="description" disabled={true} value={wishlist.description}/>
            <button hidden={!config.access.wishlist.edit}>{t("registry_edit")}</button>
        </form>
        <ItemContainer itemIdList={wishlist.itemIds} config={config}/>
    </>
}
