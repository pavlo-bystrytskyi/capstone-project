import {ChangeEvent, useEffect, useState} from "react";
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
import {Alert, Button, Col, Form, Row} from "react-bootstrap";
import ToastVariant from "../../../context/toast/ToastVariant.tsx";
import useToast from "../../../context/toast/UseToast.tsx";

export default function BaseEdit(
    {
        onSuccess,
        config
    }: {
        readonly onSuccess: (data: RegistryIdData) => void;
        readonly config: RegistryConfig;
    }) {
    const {t} = useTranslation();

    const {addToast} = useToast();

    const saveAiProcessingState = (status: boolean) => {
        return localStorage.setItem('aiProcessingState', status ? "true" : "false");
    }

    const loadAiProcessingState = () => {
        return localStorage.getItem('aiProcessingState') == "true";
    }

    const [isAiProcessingEnabled, setIsAiProcessingEnabled] = useState(loadAiProcessingState());

    const params = useParams();

    const id: string | undefined = params.id;

    const {
        register,
        handleSubmit,
        reset,
        setValue,
        formState: {errors}
    } = useForm<RegistryRestricted>({
        resolver: yupResolver(registryFormSchema)
    });

    const onSubmit: SubmitHandler<RegistryRestricted> = (data: RegistryRestricted) => {
        const payload = {...data};

        const request = id
            ? axios.put<RegistryIdData>(`${config.wishlist.url}/${id}`, payload)
            : axios.post<RegistryIdData>(config.wishlist.url, payload);

        request
            .then((response) => {
                onSuccess(response.data);
                addToast(t("toast_registry_save_successful"), ToastVariant.SUCCESS);
            })
            .catch((error) => {
                console.error('Error fetching data:', error);
                addToast(t("toast_registry_save_failed"), ToastVariant.ERROR);
            });
    };

    const [itemIdList, setItemIdList] = useState<ItemIdContainer[]>([]);

    const loadWishlist = () => {
        if (!id) return;
        axios.get<RegistryRestricted>(`${config.wishlist.url}/${id}`).then((response) => {
            reset(response.data);
            setItemIdList(response.data.itemIds);
        }).catch((error) => {
            console.error('Error fetching data:', error)
            addToast(t("toast_registry_load_failed"), ToastVariant.ERROR);
        });
    };

    const handleSwitchChange = (event: ChangeEvent<HTMLInputElement>) => {
        saveAiProcessingState(event.target.checked);
        setIsAiProcessingEnabled(event.target.checked);
    };

    useEffect(() => {
        setValue("itemIds", itemIdList);
    }, [itemIdList, setValue]);

    useEffect(loadWishlist, [id]);

    return (
        <Row
            className="w-100 justify-content-center"
            style={{maxWidth: '80%', height: 'auto'}}
        >
            <Col>
                <div className="p-3 shadow-lg rounded bg-white">
                    <Form className="registry-form" onSubmit={handleSubmit(onSubmit)}>
                        <Form.Group as={Row} controlId="title" className="mb-3 align-items-center">
                            <Form.Label column sm={2} className="text-end">
                                {t("registry_name")}
                            </Form.Label>
                            <Col sm={10}>
                                <Form.Control
                                    type="text"
                                    {...register("title")}
                                    isInvalid={!!errors.title?.message}
                                />
                                {errors.title?.message && (
                                    <Form.Control.Feedback type="invalid">
                                        {t(errors.title.message)}
                                    </Form.Control.Feedback>
                                )}
                            </Col>
                        </Form.Group>
                        <Form.Group as={Row} controlId="description" className="mb-3 align-items-center">
                            <Form.Label column sm={2} className="text-end">
                                {t("registry_description")}
                            </Form.Label>
                            <Col sm={10}>
                                <Form.Control
                                    as="textarea"
                                    rows={10}
                                    {...register("description")}
                                    isInvalid={!!errors.description?.message}
                                />
                                {errors.description?.message && (
                                    <Form.Control.Feedback type="invalid">
                                        {t(errors.description.message)}
                                    </Form.Control.Feedback>
                                )}
                            </Col>
                        </Form.Group>
                        {errors.itemIds?.message && (
                            <Row className="mb-3">
                                <Col sm={{span: 10, offset: 2}}>
                                    <Alert variant="danger">
                                        {t(errors.itemIds.message)}
                                    </Alert>
                                </Col>
                            </Row>
                        )}
                        <Row className="mb-3 align-items-center">
                            <Col sm={{span: 2, offset: 2}}>
                                <Button variant="primary" type="submit" className="me-2">
                                    {t("registry_save")}
                                </Button>
                            </Col>
                            <Col sm={{span: 8}} className="d-flex align-items-center">
                                <Form.Check
                                    type="switch"
                                    id="enable-ai"
                                    label={t("enable_ai_hint")}
                                    className="form-switch"
                                    checked={isAiProcessingEnabled}
                                    onChange={handleSwitchChange}
                                />
                            </Col>
                        </Row>
                    </Form>

                    <ItemContainer config={config} itemIdList={itemIdList} setItemIdList={setItemIdList}
                                   isAiProcessingEnabled={isAiProcessingEnabled}/>
                </div>
            </Col>
        </Row>
    );
}
