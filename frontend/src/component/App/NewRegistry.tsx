import {useTranslation} from "react-i18next";
import {ChangeEvent, FormEvent, useState} from "react";
import axios from "axios";
import NewRegistryData from "../../dto/NewRegistryData.tsx";
import RegistryData from "../../dto/RegistryData.tsx";

export default function NewRegistry({onSuccess}: { readonly onSuccess: (data: NewRegistryData) => void }) {
    const {t} = useTranslation();
    const [registryData, setRegistryData] = useState<RegistryData>(
        {
            title: "",
            description: ""
        }
    );

    const handleSubmit = function (event: FormEvent) {
        event.preventDefault();
        const payload = {
            ...registryData,
            itemIds: []
        }
        axios.post<NewRegistryData>('/api/wishlist', payload)
            .then(response => {
                onSuccess(response.data);
            })
            .catch(error => {
                console.error('Error fetching data:', error);
            });
    }

    const handleDataChange = (event: ChangeEvent<HTMLInputElement>) => {
        const {name, value} = event.target;
        setRegistryData((prevState) => ({
            ...prevState,
            [name]: value,
        }));
    };

    return (
        <div className="new-registry-guest">
            <h2>{t("new_registry")}</h2>
            <form className="registry-form" onSubmit={handleSubmit}>
                <label htmlFor="title">{t("registry_name")}</label>
                <input type="text" name="title" onChange={(e) => handleDataChange(e)}/>
                <label htmlFor="description">{t("registry_description")}</label>
                <input type="text" name="description" onChange={(e) => handleDataChange(e)}/>
                <label htmlFor="product">{t("registry_item")}</label>
                <button>{t("create")}</button>
            </form>
        </div>
    );
}