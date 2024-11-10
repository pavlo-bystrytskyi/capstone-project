import {useTranslation} from "react-i18next";
import {ChangeEvent, FormEvent, useEffect, useState} from "react";
import axios from "axios";
import RegistryIdData from "../../../dto/RegistryIdData.tsx";
import Registry from "../../../type/Registry.tsx";
import ItemContainer from "./BaseEdit/ItemContainer.tsx";
import {useParams} from "react-router-dom";
import RegistryConfig from "../../../type/RegistryConfig.tsx";
import ItemId from "../../../type/ItemId.tsx";

export default function BaseEdit(
    {
        onSuccess,
        config
    }: {
        readonly onSuccess: (data: RegistryIdData) => void
        readonly config: RegistryConfig
    }
) {
    const {t} = useTranslation();
    const params = useParams();
    const id: string | undefined = params.id;
    const [wishlist, setWishlist] = useState<Registry>(
        {
            title: "",
            description: "",
            privateItemIds: [],
            publicItemIds: [],
        }
    );
    const [itemIdList, setItemIdList] = useState<ItemId[]>([]);
    const handleSubmit = function (event: FormEvent) {
        event.preventDefault();
        const payload = {
            ...wishlist,
            privateItemIds: itemIdList.map(
                (item: ItemId) => item.privateId
            ),
            publicItemIds: itemIdList.map(
                (item: ItemId) => item.publicId
            ),
        }
        if (id) {
            axios.put<RegistryIdData>(`${config.wishlist.url}/${id}`, payload)
                .then(response => {
                    onSuccess(response.data);
                })
                .catch(error => {
                    console.error('Error fetching data:', error);
                })
        } else {
            axios.post<RegistryIdData>(config.wishlist.url, payload)
                .then(response => {
                    onSuccess(response.data);
                })
                .catch(error => {
                    console.error('Error fetching data:', error);
                });
        }
    }
    const handleDataChange = (event: ChangeEvent<HTMLInputElement>) => {
        const {name, value} = event.target;
        setWishlist((prevState) => ({
            ...prevState,
            [name]: value,
        }));
    };
    const loadWishlist = function () {
        if (!id) return;
        axios.get<Registry>(`${config.wishlist.url}/${id}`).then(
            (response) => {
                setWishlist(response.data);
                setInitialItems(response.data.privateItemIds);
            }
        ).catch(error => {
            console.error('Error fetching data:', error);
        });
    }
    const setInitialItems = function (privateIdList: string[]) {
        setItemIdList(
            privateIdList.map((privateItemId: string) => {
                    return {privateId: privateItemId, publicId: ""};
                }
            )
        );
    }

    useEffect(
        loadWishlist,
        [id]
    );

    return (
        <>
            <form className="registry-form" onSubmit={handleSubmit}>
                <label htmlFor="title">{t("registry_name")}</label>
                <input type="text" name="title" value={wishlist.title} onChange={(e) => handleDataChange(e)}/>
                <label htmlFor="description">{t("registry_description")}</label>
                <input type="text" name="description" value={wishlist.description}
                       onChange={(e) => handleDataChange(e)}/>
                <button>{t("registry_save")}</button>
            </form>
            <ItemContainer itemIdList={itemIdList} setItemIdList={setItemIdList}/>
        </>
    );
}
