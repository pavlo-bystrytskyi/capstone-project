import {useTranslation} from "react-i18next";
import User from "../../../type/User.tsx";
import {useEffect, useState} from "react";
import Registry from "../../../type/Registry.tsx";
import axios from "axios";
import ListElement from "../ViewList/ListElement.tsx";

export default function ViewList({user}: { user: User }) {
    const {t} = useTranslation();
    const [registryList, setRegistryList] = useState<Registry[]>([]);

    const loadRegistryList = function () {
        axios.get<Registry[]>("/api/user/wishlist")
            .then(
                (result) => setRegistryList(result.data)
            )
            .catch(error => {
                console.error('Error fetching data:', error);
            });
    }

    useEffect(loadRegistryList, [user]);

    return <>
        <h2>{t("registry_list")}</h2>
        <ul>
            {registryList.map(
                (registry: Registry) => <li>{<ListElement registry={registry}/>}</li>
            )}
        </ul>
    </>
}