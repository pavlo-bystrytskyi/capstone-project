import {useEffect, useState} from "react";
import {SubmitHandler, useForm} from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup";
import axios from "axios";
import {useTranslation} from "react-i18next";
import {useParams} from "react-router-dom";
import RegistryIdData from "../../../type/RegistryIdData.tsx";
import RegistryConfig from "../../../type/RegistryConfig.tsx";
import ItemIdContainer from "../../../type/ItemIdContainer.tsx";
import ItemContainer from "./BaseEdit/ItemContainer.tsx";
import registryFormSchema from "../../../schema/RegistryFormSchema.tsx";
import RegistryRestricted from "../../../type/RegistryRestricted.tsx";

export default function BaseEdit(
    {
        onSuccess,
        config
    }: {
        readonly onSuccess: (data: RegistryIdData) => void;
        readonly config: RegistryConfig;
    }) {
    const {t} = useTranslation();

    const params = useParams();

    const id: string | undefined = params.id;

    const {register, handleSubmit, reset, setValue, formState: {errors}} = useForm<RegistryRestricted>({
        resolver: yupResolver(registryFormSchema)
    });

    const onSubmit: SubmitHandler<RegistryRestricted> = (data) => {
        const payload = {...data};

        const request = id
            ? axios.put<RegistryIdData>(`${config.wishlist.url}/${id}`, payload)
            : axios.post<RegistryIdData>(config.wishlist.url, payload);

        request
            .then((response) => {
                onSuccess(response.data);
            })
            .catch((error) => {
                console.error('Error fetching data:', error);
            });
    };

    const [itemIdList, setItemIdList] = useState<ItemIdContainer[]>([]);

    const loadWishlist = () => {
        if (!id) return;
        axios.get<RegistryRestricted>(`${config.wishlist.url}/${id}`).then((response) => {
            reset(response.data);
            setItemIdList(response.data.itemIds);
        }).catch((error) => {
            console.error('Error fetching data:', error);
        });
    };

    useEffect(() => {
        setValue("itemIds", itemIdList);
    }, [itemIdList, setValue]);

    useEffect(loadWishlist, [id]);

    return (
        <>
            <form className="registry-form" onSubmit={handleSubmit(onSubmit)}>
                <label htmlFor="title">{t("registry_name")}</label>
                <input {...register("title")} />
                {errors.title?.message && <p>{t(errors.title.message)}</p>}
                <label htmlFor="description">{t("registry_description")}</label>
                <input {...register("description")} />
                {errors.description?.message && <p>{t(errors.description.message)}</p>}
                {errors.itemIds?.message && <p>{t(errors.itemIds.message)}</p>}
                <button type="submit">{t("registry_save")}</button>
            </form>
            <ItemContainer config={config} itemIdList={itemIdList} setItemIdList={setItemIdList}/>
        </>
    );
}
