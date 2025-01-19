import {useTranslation} from "react-i18next";
import {useEffect, useState} from "react";
import Registry from "../../../type/Registry.tsx";
import axios from "axios";
import ListElement from "../ViewList/ListElement.tsx";
import {Col, Row} from "react-bootstrap";
import useAuth from "../../../context/auth/UserAuth.tsx";
import ProtectedComponent from "../../ProtectedComponent.tsx";
import ToastVariant from "../../../context/toast/ToastVariant.tsx";
import useToast from "../../../context/toast/UseToast.tsx";

export default function ViewList() {
    const {t} = useTranslation();
    const {addToast} = useToast();
    const [registryList, setRegistryList] = useState<Registry[]>([]);
    const {user} = useAuth();
    const loadRegistryList = function () {
        axios.get<Registry[]>("/api/user/wishlist")
            .then(
                (result) => setRegistryList(result.data)
            )
            .catch(error => {
                console.error('Error fetching data:', error);
                addToast(t("toast_registry_load_failed"), ToastVariant.ERROR);
            });
    }
    useEffect(loadRegistryList, [user]);

    return (
        <ProtectedComponent>
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
                            {registryList.length > 0 ?
                                registryList.map((registry: Registry) => (
                                    <Row key={registry.publicId}>
                                        <ListElement registry={registry}/>
                                    </Row>
                                ))
                                : <Row>
                                    {t("registry_list_empty")}
                                </Row>
                            }
                        </Col>
                    </Row>
                </Col>
            </Row>
        </ProtectedComponent>
    );
}
