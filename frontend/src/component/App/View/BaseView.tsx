import {useEffect, useState} from "react";
import {useForm} from "react-hook-form";
import {yupResolver} from "@hookform/resolvers/yup";
import axios from "axios";
import {useTranslation} from "react-i18next";
import {useNavigate, useParams} from "react-router-dom";
import RegistryConfig from "../../../type/RegistryConfig.tsx";
import registryFormSchema from "../../../schema/RegistryFormSchema.tsx";
import RegistryRestricted from "../../../type/RegistryRestricted.tsx";
import {Button, Col, Form, Row} from "react-bootstrap";
import Registry from "../../../type/Registry.tsx";
import {emptyRegistry} from "../../../type/EmptyRegistry.tsx";
import ItemContainer from "./ItemContainer.tsx";
import useToast from "../../../context/toast/UseToast.tsx";
import ToastVariant from "../../../context/toast/ToastVariant.tsx";

export default function BaseView({config}: { readonly config: RegistryConfig }) {
    const {t} = useTranslation();
    const {addToast} = useToast();
    const params = useParams();
    const id: string | undefined = params.id;
    const navigate = useNavigate();
    const [wishlist, setWishlist] = useState<Registry>(emptyRegistry);
    const loadWishlist = function () {
        axios.get<Registry>(`${config.wishlist.url}/${id}`).then(
            (response) => setWishlist(response.data)
        ).catch((error) => {
            console.error('Error fetching data:', error);
            addToast(t("toast_registry_load_failed"), ToastVariant.ERROR);
        });
    }
    useEffect(
        loadWishlist,
        [id]
    );
    const openEditPage = function () {
        navigate(`${config.access.wishlist.edit.url}/${id}`);
    }
    const editAllowed: boolean = config.access.wishlist.edit.allowed;

    useForm<RegistryRestricted>({
        resolver: yupResolver(registryFormSchema),
        defaultValues: wishlist,
    });

    useEffect(loadWishlist, [id]);

    return (
        <Row
            className="w-100 justify-content-center"
            style={{maxWidth: '80%', height: 'auto'}}
        >
            <Col>
                <div className="p-3 shadow-lg rounded bg-white">
                    <Form className="registry-form" onSubmit={openEditPage}>
                        <Form.Group as={Row} controlId="title" className="mb-3 align-items-center">
                            <Form.Label column sm={2} className="text-end">
                                {t("registry_name")}
                            </Form.Label>
                            <Col sm={10}>
                                <Form.Control
                                    type="text"
                                    value={wishlist?.title}
                                    disabled
                                />
                            </Col>
                        </Form.Group>
                        <Form.Group as={Row} controlId="description" className="mb-3 align-items-center">
                            <Form.Label column sm={2} className="text-end">
                                {t("registry_description")}
                            </Form.Label>
                            <Col sm={10}>
                                <Form.Control
                                    as="textarea"
                                    rows={3}
                                    value={wishlist?.description}
                                    disabled
                                />
                            </Col>
                        </Form.Group>
                        <Row className="mb-3">
                            <Col sm={{span: 10, offset: 2}}>
                                {editAllowed && (
                                    <Button variant="primary" type="submit">
                                        {t("registry_edit")}
                                    </Button>
                                )}
                            </Col>
                        </Row>
                    </Form>
                    <ItemContainer itemIdList={wishlist.itemIds} config={config}/></div>
            </Col>
        </Row>
    );
}
