import {useTranslation} from "react-i18next";
import User from "../../../type/User.tsx";
import {useEffect, useState} from "react";
import Registry from "../../../type/Registry.tsx";
import axios from "axios";
import ListElement from "../ViewList/ListElement.tsx";
import {Col, Row} from "react-bootstrap";

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

    return (
        <Row className="w-100 justify-content-center px-3">
            <Col
                className="p-5 shadow-lg rounded bg-white"
                style={{maxWidth: '800px', width: '100%'}}
            >
                <Row className="text-center mb-4">
                    <h2>{t("registry_list")}</h2>
                </Row>
                <Row className="private-data mb-4">
                    <Col>
                        {registryList.map((registry: Registry) => (
                            <Row key={registry.publicId}>
                                <ListElement registry={registry}/>
                            </Row>
                        ))}
                    </Col>
                </Row>
            </Col>
        </Row>
    );
}
