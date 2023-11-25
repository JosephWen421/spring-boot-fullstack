import {Spinner, Text, Wrap, WrapItem} from "@chakra-ui/react";
import SidebarWithHeader from "./components/shared/SideBar.jsx";
import {useEffect, useState} from "react";
import {getCustomers} from "./services/client.js";
import CardWithImage from "./components/Card.jsx";
import CreateCustomerDrawer from "./components/CreateCustomerDrawer.jsx";
import {errorNotification} from "./services/notification.js";

const App = () => {
  const [customers, setCustomers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [err, setError] = useState("");

  const fetchCustomers = () => {
    setLoading(true);
    setTimeout(() => {
      getCustomers().then(res => {
        setCustomers(res.data)
        console.log(res.data)
      }).catch(err => {
        console.log(err)
        setError(err)
        errorNotification(
            err.code,
            err.response.data.message
        )
      }).finally(() => {
        setLoading(false)
      })
    }, 300)
  }

  useEffect(() => {
    fetchCustomers();
  }, []);

  if (loading) {
    return (
        <SidebarWithHeader>
          <Spinner
              thickness='4px'
              speed='0.65s'
              emptyColor='gray.200'
              color='blue.500'
              size='xl'
          />
        </SidebarWithHeader>
    )
  }

  if (err) {
    return (
        <SidebarWithHeader>
          <CreateCustomerDrawer
              fetchCustomers={fetchCustomers}
          />
          <Text mt={5}>Oppose there was an error</Text>
        </SidebarWithHeader>
    )
  }

  if (customers.length <= 0) {
    return (
        <SidebarWithHeader>
          <CreateCustomerDrawer
              fetchCustomers={fetchCustomers}
          />
          <Text mt={5}>No customer available</Text>
        </SidebarWithHeader>
    )
  }

  return (
      <SidebarWithHeader>
        <CreateCustomerDrawer
            fetchCustomers={fetchCustomers}
        />
        <Wrap spacing={"30px"}>
          {customers.map((customer, index) => (
              <WrapItem
                  key={index}
              >
                <CardWithImage
                    {...customer}
                    imageNumber={customer.id}
                    fetchCustomers={fetchCustomers}
                />
              </WrapItem>
          ))}
        </Wrap>
      </SidebarWithHeader>
  )
}

export default App