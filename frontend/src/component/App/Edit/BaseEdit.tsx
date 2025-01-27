import {ChangeEvent, useEffect, useState} from "react";
import {Controller, SubmitHandler, useForm} from "react-hook-form";
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
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

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
        return localStorage.getItem('aiProcessingState') === "true";
    };

    const [isAiProcessingEnabled, setIsAiProcessingEnabled] = useState(loadAiProcessingState());

    const params = useParams();

    const id: string | undefined = params.id;

    const {
        register,
        handleSubmit,
        reset,
        setValue,
        control,
        watch,
        formState: {errors}
    } = useForm<RegistryRestricted>({
        resolver: yupResolver(registryFormSchema),
        defaultValues: {
            title: "",
            description: "",
            active: true,
            deactivationDate: null,
            itemIds: []
        }
    });

    const isActive = watch("active", true);

    const [isAutoDisable, setIsAutoDisable] = useState(false);

    const onSubmit: SubmitHandler<RegistryRestricted> = (data: RegistryRestricted) => {
        const payload = {...data};
        payload.active = isActive;
        if (!isAutoDisable || !isActive) {
            payload.deactivationDate = null;
        }

        const request = id
            ? axios.put<RegistryIdData>(`${config.wishlist.url}/${id}`, payload)
            : axios.post<RegistryIdData>(config.wishlist.url, payload);

        request
            .then((response) => {
                onSuccess(response.data);
                addToast(t("toast_registry_save_successful"), ToastVariant.SUCCESS);
            })
            .catch((error) => {
                console.error('Error saving data:', error);
                addToast(t("toast_registry_save_failed"), ToastVariant.ERROR);
            });
    };

    const [itemIdList, setItemIdList] = useState<ItemIdContainer[]>([]);

    const loadWishlist = () => {
        if (!id) return;
        axios.get<RegistryRestricted>(`${config.wishlist.url}/${id}`).then((response) => {
            response.data.deactivationDate = response.data.deactivationDate
                && new Date(response.data.deactivationDate);
            reset(response.data);
            setItemIdList(response.data.itemIds);
            setIsAutoDisable(response.data.deactivationDate !== null);
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
                        {id && (
                            <Form.Group as={Row} controlId="isActive" className="mb-3 align-items-center">
                                <Form.Label column sm={2} className="text-end">
                                    {t("active")}
                                </Form.Label>
                                <Col sm={10}>
                                    <Form.Check
                                        type="switch"
                                        id="isActiveSwitch"
                                        label={t("active_hint")}
                                        checked={isActive}
                                        onChange={(e) => setValue("active", e.target.checked)}
                                    />
                                </Col>
                            </Form.Group>
                        )}
                        {isActive && (
                            <Form.Group as={Row} controlId="isAutoDisable" className="mb-3 align-items-center">
                                <Form.Label column sm={2} className="text-end">
                                    {t("auto_disable")}
                                </Form.Label>
                                <Col sm={10}>
                                    <Form.Check
                                        type="switch"
                                        id="isAutoDisableSwitch"
                                        label={t("auto_disable_hint")}
                                        checked={isAutoDisable}
                                        onChange={() => setIsAutoDisable((prev) => !prev)}
                                    />
                                </Col>
                            </Form.Group>
                        )}
                        {isActive && isAutoDisable && (
                            <Form.Group as={Row} controlId="timePicker" className="mb-3 align-items-center">
                                <Form.Label column sm={2} className="text-end">
                                    {t("select_time")}
                                </Form.Label>
                                <Col sm={10}>
                                    <Controller
                                        name="deactivationDate"
                                        control={control}
                                        defaultValue={(() => {
                                            const now = new Date();
                                            now.setMinutes(0, 0, 0);
                                            now.setHours(now.getHours() + 1);
                                            return now;
                                        })()}
                                        render={({field}) => (
                                            <DatePicker
                                                {...field}
                                                selected={field.value}
                                                showTimeSelect
                                                timeIntervals={60}
                                                minDate={new Date()}
                                                minTime={new Date(new Date().setMinutes(0, 0, 0) + 60 * 60 * 1000)}
                                                maxTime={new Date(new Date().setHours(23, 0, 0, 0))}
                                                dateFormat={t("date_format")}
                                                timeCaption={t("time_caption")}
                                                timeFormat={t("time_format")}
                                                showYearDropdown
                                                showMonthDropdown
                                                dropdownMode="select"
                                                className={`form-control ${
                                                    errors.deactivationDate?.message ? "is-invalid" : ""
                                                }`}
                                            />
                                        )}
                                    />
                                    {errors.deactivationDate?.message && (
                                        <Form.Control.Feedback type="invalid">
                                            {t(errors.deactivationDate.message)}
                                        </Form.Control.Feedback>
                                    )}
                                </Col>
                            </Form.Group>
                        )}
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
